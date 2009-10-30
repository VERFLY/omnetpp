/*
 * Copyright (c) 2003 The Visigoth Software Society. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Visigoth Software Society (http://www.visigoths.org/)."
 *    Alternately, this acknowledgement may appear in the software 
 *    itself, if and wherever such third-party acknowledgements 
 *    normally appear.
 *
 * 4. Neither the name "FreeMarker", "Visigoth", nor any of the names 
 *    of the project contributors may be used to endorse or promote 
 *    products derived from this software without prior written 
 *    permission. For written permission, please contact 
 *    visigoths@visigoths.org.
 *
 * 5. Products derived from this software may not be called 
 *    "FreeMarker" or "Visigoth" nor may "FreeMarker" or "Visigoth" 
 *    appear in their names without prior written permission of the 
 *    Visigoth Software Society.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE VISIGOTH SOFTWARE SOCIETY OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Visigoth Software Society. For more
 * information on the Visigoth Software Society, please see
 * http://www.visigoths.org/
 */

package freemarker.eclipse.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.swt.graphics.Color;

/**
 * The XML comment scanner. This class handles the lexical scanning
 * of the XML comments, separating out FreeMarker constructs into
 * their own tokens (with their own colors).
 *
 * @version $Id: XmlCommentScanner.java,v 1.2 2004/02/05 00:16:23 stephanmueller Exp $
 * @author <a href="mailto:per&#64;percederberg.net">Per Cederberg</a>
 */
public class XmlCommentScanner extends RuleBasedScanner {

    /**
     * Creates a new XML comment scanner.
     *
     * @param manager        the token manager to use
     */
    public XmlCommentScanner(ITokenManager manager) {
        ArrayList  rules = new ArrayList();
        IRule[]    result;
        IToken      comment;
        IToken      directive;
        IToken      interpolation;
        Color      color;

        // Retrieve tokens
        comment = manager.getCommentToken();
        directive = manager.getDirectiveToken();
        interpolation = manager.getInterpolationToken();

        // Create rules for FreeMarker constructs
        rules.add(new MultiLineRule("<#--", "-->", comment));
        rules.add(new DirectiveRule(directive));
        rules.add(new InterpolationRule(interpolation));

        // Set scanner rules
        result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }
}
