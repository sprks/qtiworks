/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.group.content;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiModelException;
import uk.ac.ed.ph.jqtiplus.group.AbstractNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Group of content children.
 * 
 * @author Jonathon Hare
 */
public abstract class AbstractContentNodeGroup extends AbstractNodeGroup {

    private static final long serialVersionUID = -630489519873000102L;

    /**
     * Constructs group.
     * 
     * @param parent parent of created group
     * @param name name of node group
     * @param required is group required
     */
    public AbstractContentNodeGroup(XmlNode parent, String name, boolean required) {
        super(parent, name, required);
    }

    /**
     * Constructs group.
     * 
     * @param parent parent of created group
     * @param name name of node group
     * @param minimum minimum number of children
     * @param maximum maximum number of children
     */
    public AbstractContentNodeGroup(XmlNode parent, String name, Integer minimum, Integer maximum) {
        super(parent, name, minimum, maximum);
    }

    @Override
    public void load(Element node, LoadingContext context) {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE && getAllSupportedClasses().contains(childNode.getLocalName())) {
                try {
                    final XmlNode child = createChild((Element) childNode, context.getJqtiExtensionManager());
                    getChildren().add(child);
                    child.load((Element) childNode, context);
                }
                catch (final QtiModelException e) {
                    context.modelBuildingError(e, (Element) childNode);
                }
            }
            else if (childNode.getNodeType() == Node.TEXT_NODE && getAllSupportedClasses().contains(TextRun.DISPLAY_NAME)) {
                try {
                    final TextRun child = (TextRun) create(TextRun.DISPLAY_NAME);
                    getChildren().add(child);
                    child.load((Text) childNode);
                }
                catch (final Exception e) {
                    throw new QtiLogicException("Expected to be able to add a " + TextRun.class + " here");
                }
            }
        }
    }
}