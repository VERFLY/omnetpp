# Microsoft Developer Studio Project File - Name="topo" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=topo - Win32 Debug Tkenv
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE
!MESSAGE NMAKE /f "topo.mak".
!MESSAGE
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE
!MESSAGE NMAKE /f "topo.mak" CFG="topo - Win32 Debug Tkenv"
!MESSAGE
!MESSAGE Possible choices for configuration are:
!MESSAGE
!MESSAGE "topo - Win32 Debug Tkenv" (based on "Win32 (x86) Console Application")
!MESSAGE "topo - Win32 Debug Cmdenv" (based on "Win32 (x86) Console Application")
!MESSAGE "topo - Win32 Release Tkenv" (based on "Win32 (x86) Console Application")
!MESSAGE "topo - Win32 Release Cmdenv" (based on "Win32 (x86) Console Application")
!MESSAGE

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "topo___Win32_Debug_Tkenv"
# PROP BASE Intermediate_Dir "topo___Win32_Debug_Tkenv"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "."
# PROP Intermediate_Dir "debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GR /GX /ZI /Od /I "d:/home/omnetpp/include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD CPP /nologo /W3 /Gm /GR /GX /ZI /Od /I "d:/home/omnetpp/include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 sim_std.lib envir.lib tkenv.lib tcl84.lib tk84.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /stack:0x1000000 /subsystem:console /debug /machine:I386 /pdbtype:sept /libpath:"d:/home/omnetpp/lib" /libpath:"d:/home/tools/Tcl-8.4.1/lib"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "topo___Win32_Debug_Cmdenv"
# PROP BASE Intermediate_Dir "topo___Win32_Debug_Cmdenv"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "."
# PROP Intermediate_Dir "debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GR /GX /ZI /Od /I "d:/home/omnetpp/include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD CPP /nologo /W3 /Gm /GR /GX /ZI /Od /I "d:/home/omnetpp/include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 sim_std.lib envir.lib cmdenv.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /stack:0x1000000 /subsystem:console /debug /machine:I386 /pdbtype:sept /libpath:"d:/home/omnetpp/lib"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "topo___Win32_Release_Tkenv"
# PROP BASE Intermediate_Dir "topo___Win32_Release_Tkenv"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "."
# PROP Intermediate_Dir "release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GR /GX /O2 /I "d:/home/omnetpp/include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /W3 /GR /GX /O2 /I "d:/home/omnetpp/include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 sim_std.lib envir.lib tkenv.lib tcl84.lib tk84.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /stack:0x1000000 /subsystem:console /machine:I386 /libpath:"d:/home/omnetpp/lib" /libpath:"d:/home/tools/Tcl-8.4.1/lib"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "topo___Win32_Release_Cmdenv"
# PROP BASE Intermediate_Dir "topo___Win32_Release_Cmdenv"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "."
# PROP Intermediate_Dir "release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GR /GX /O2 /I "d:/home/omnetpp/include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /W3 /GR /GX /O2 /I "d:/home/omnetpp/include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 sim_std.lib envir.lib cmdenv.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /stack:0x1000000 /subsystem:console /machine:I386 /libpath:"d:/home/omnetpp/lib"

!ENDIF

# Begin Target

# Name "topo - Win32 Debug Tkenv"
# Name "topo - Win32 Debug Cmdenv"
# Name "topo - Win32 Release Tkenv"
# Name "topo - Win32 Release Cmdenv"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\binarytree.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\binarytree.ned
InputName=binarytree

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\binarytree_n.cpp
# End Source File
# Begin Source File

SOURCE=.\chain.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\chain.ned
InputName=chain

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\chain_n.cpp
# End Source File
# Begin Source File

SOURCE=.\fullgraph.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\fullgraph.ned
InputName=fullgraph

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\fullgraph_n.cpp
# End Source File
# Begin Source File

SOURCE=.\hexgrid.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\hexgrid.ned
InputName=hexgrid

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\hexgrid_n.cpp
# End Source File
# Begin Source File

SOURCE=.\mesh.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\mesh.ned
InputName=mesh

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\mesh_n.cpp
# End Source File
# Begin Source File

SOURCE=.\node.cpp
# End Source File
# Begin Source File

SOURCE=.\randomgraph.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\randomgraph.ned
InputName=randomgraph

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\randomgraph_n.cpp
# End Source File
# Begin Source File

SOURCE=.\star.ned

!IF  "$(CFG)" == "topo - Win32 Debug Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Debug Cmdenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Tkenv"

!ELSEIF  "$(CFG)" == "topo - Win32 Release Cmdenv"

# Begin Custom Build - NED Compiling $(InputPath)
InputPath=.\star.ned
InputName=star

"$(InputName)_n.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	d:\home\omnetpp\bin\nedc.exe -s _n.cpp $(InputName).ned

# End Custom Build

!ENDIF

# End Source File
# Begin Source File

SOURCE=.\star_n.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# End Target
# End Project
