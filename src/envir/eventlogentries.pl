#
# Generates eventlog writer code from eventlogentries.txt
#
# Author: Andras Varga, 2006
#

open(FILE, "../eventlog/eventlogentries.txt");


#
# Read input file
#
while (<FILE>)
{
   chomp;
   if ($_ =~ /^ *$/)
   {
      # blank line
   }
   elsif ($_ =~ /^ *\/\//)
   {
      # comment
   }
   elsif ($_ =~ /^([\w]+) +([\w]+) *$/)
   {
      $classCode = $1;
      $className = $2;
      $classHasOptField = 0;
      print "$classCode $className\n";
   }
   elsif ($_ =~ /^ *{ *$/)
   {
      print "{\n";
   }
   elsif ($_ =~ /^ +([\w#]+) +([\w]+) +([\w]+)( +opt)? *$/)
   {
      $fieldCode = $1;
      $fieldType = $2;
      $fieldName = $3;
      $fieldOpt  = ($4 ne "");

      if ($fieldType eq "string")
      {
         $fieldPrintfType = "%s";
      }
      elsif ($fieldType eq "long")
      {
         $fieldPrintfType = "%ld";
      }
      elsif ($fieldType eq "int")
      {
         $fieldPrintfType = "%d";
      }
      elsif ($fieldType eq "simtime_t")
      {
         $fieldPrintfType = "%s";
      }

      if ($fieldOpt) {
         $classHasOptField = 1;
      }

      $fieldCType = $fieldType;
      $fieldCType =~ s/string/const char */;
      $field = {
         CODE => $fieldCode,
         TYPE => $fieldType,
         CTYPE => $fieldCType,
         PRINTFTYPE => $fieldPrintfType,
         NAME => $fieldName,
         OPT => $fieldOpt,
      };

      push(@fields, $field);
      print " $fieldCode $fieldType $fieldName $fieldOpt\n";
   }
   elsif ($_ =~ /^ *} *$/)
   {
      $class = {
         CODE => $classCode,
         NAME => $className,
         HASOPT => $classHasOptField,
         FIELDS => [ @fields ],
      };
      push(@classes, $class);
      @fields = ();
      print "}\n";
   }
   else
   {
       die "unrecognized line \"$_\"";
   }
}

close(FILE);



#
# Write eventlogwriter.h file
#

open(H, ">eventlogwriter.h");

print H makeFileBanner("eventlogwriter.h");
print H
"#ifndef __EVENTLOGWRITER_H_
#define __EVENTLOGWRITER_H_

class EventLogWriter
{
  public:
";

foreach $class (@classes)
{
   print H "    static void " . makeMethodDecl($class,0) . ";\n";
   print H "    static void " . makeMethodDecl($class,1) . ";\n" if ($class->{HASOPT});
}

print H "};

#endif
";

close(H);



#
# Write eventlogwriter.cc file
#

open(CC, ">eventlogwriter.cc");

print CC makeFileBanner("eventlogwriter.cc");
print CC "
#include \"eventlogwriter.h\"

#ifdef CHECK
#undef CHECK
#endif
#define CHECK(fprintf)    if (fprintf<0) throw new cRuntimeError(\"Cannot write event log file, disk full?\");

";

#FIXME: use SIMTIME_STR(), quote strings, etc! and change omnetapp.cc to use this

foreach $class (@classes)
{
   print CC makeMethodImpl($class,0);
   print CC makeMethodImpl($class,1) if ($class->{HASOPT});
}

close(CC);


sub makeMethodImpl ()
{
   my $class = shift;
   my $optfields = shift;

   my $txt = "void EventLogWriter::" . makeMethodDecl($class,$optfields) . "\n";
   $txt .= "{\n";
   $txt .= "    CHECK(fprintf(f, \"$class->{CODE}";
   foreach $field (@{ $class->{FIELDS} })
   {
      $txt .= " $field->{CODE} $field->{PRINTFTYPE}" if ($optfields || !$field->{OPT});
   }
   $txt .= "\\n\"";
   foreach $field (@{ $class->{FIELDS} })
   {
      $txt .= ", $field->{NAME}" if ($optfields || !$field->{OPT});
   }
   $txt .= "));\n";
   $txt .= "}\n\n";
   $txt;
}

sub makeMethodDecl ()
{
   my $class = shift;
   my $optfields = shift;

   my $txt = "record$class->{NAME}(FILE *f";
   foreach $field (@{ $class->{FIELDS} })
   {
      $txt .= ", $field->{CTYPE} $field->{NAME}" if ($optfields || !$field->{OPT});
   }
   $txt .= ")";
   $txt;
}

sub makeFileBanner ()
{
    my $ufilename = uc(shift);
    return
"//=========================================================================
// $ufilename - part of
//                  OMNeT++/OMNEST
//           Discrete System Simulation in C++
//
//  This is a generated file -- do not modify.
//
//=========================================================================

";
}


