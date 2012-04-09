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
package uk.ac.ed.ph.jqtiplus.utils;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.ForeignAttribute;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.block.ForeignElement;
import uk.ac.ed.ph.jqtiplus.node.content.BodyElement;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.CustomInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootObjectLookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class will provide some utility methods to perform deep queries or searches
 * of a {@link XmlNode} tree.
 *
 * @author David McKain
 */
public final class QueryUtils {

    /**
     * Performs a deep search starting at the given {@link XmlNode}(s) for instances of
     * the given target type.
     * <p>
     * (This used to be part of the {@link BodyElement} interface in JQTI, but didn't work
     * correctly!)
     *
     * @param searchClass type of descendant to search for
     * @param nodes {@link XmlNode}(s) to start searching from
     * @return List of all Nodes found
     */
    public static <E extends XmlNode> List<E> search(final Class<E> searchClass, Iterable<? extends XmlNode> nodes) {
        final List<E> results = new ArrayList<E>();
        walkTree(new TreeWalkNodeHandler() {
            @Override
            public boolean handleNode(XmlNode node) {
                if (searchClass.isInstance(node)) {
                    results.add(searchClass.cast(node));
                }
                /* Keep descending */
                return true;
            }
        }, nodes);
        return results;
    }

    /**
     * Performs a deep search starting at the children of given {@link XmlNode}(s) for the
     * first instance of a node of the given searchType.
     * <p>
     * Returns true if such a Node is found, false otherwise.
     *
     * @param searchClass
     * @param nodes
     * @return true if such a Node is found, false otherwise.
     */
    public static <E extends XmlNode> boolean hasDescendant(final Class<E> searchClass, Iterable<? extends XmlNode> nodes) {
        DescendentSearchHandler<E> handler = new DescendentSearchHandler<E>(searchClass);
        walkChildNodes(handler, nodes);
        return handler.wasSuccessful();
    }

    private static final class DescendentSearchHandler<E extends XmlNode> implements TreeWalkNodeHandler {

        public DescendentSearchHandler(Class<E> searchClass) {
            this.searchClass = searchClass;
        }

        private final Class<E> searchClass;
        private boolean found = false;

        @Override
        public boolean handleNode(XmlNode node) {
            if (found || searchClass.isInstance(node)) {
                /* Found, so stop searching */
                found = true;
                return false;
            }
            /* Keep searching */
            return true;
        }

        public boolean wasSuccessful() {
            return found;
        }
    }

    public static Set<JqtiExtensionPackage> findExtensionsUsed(ResolvedAssessmentItem resolvedItem) {
        Set<JqtiExtensionPackage> resultSet = findExtensionsWithin(resolvedItem.getItemLookup().extractAssumingSuccessful());
        RootObjectLookup<ResponseProcessing> rpTemplateLookup = resolvedItem.getResolvedResponseProcessingTemplateLookup();
        if (rpTemplateLookup!=null) {
            resultSet.addAll(findExtensionsWithin(rpTemplateLookup.extractAssumingSuccessful()));
        }
        return resultSet;
    }

    /**
     * Finds all {@link JqtiExtensionPackage}s used by the given {@link XmlNode}s and their
     * child Nodes.
     *
     * @param node
     */
    public static Set<JqtiExtensionPackage> findExtensionsWithin(Iterable<? extends XmlNode> nodes) {
        final Set<JqtiExtensionPackage> resultSet = new HashSet<JqtiExtensionPackage>();
        walkTree(new TreeWalkNodeHandler() {
            @Override
            public boolean handleNode(XmlNode node) {
                if (node instanceof CustomOperator) {
                    resultSet.add(((CustomOperator) node).getJqtiExtensionPackage());
                }
                else if (node instanceof CustomInteraction) {
                    resultSet.add(((CustomInteraction) node).getJqtiExtensionPackage());
                }
                /* Keep descending */
                return true;
            }
        }, nodes);
        return resultSet;
    }

    public static ForeignNamespaceSummary findForeignNamespaces(Iterable<? extends XmlNode> nodes) {
        final Set<String> elementNamespaceUris = new HashSet<String>();
        final Set<String> attributeNamespaceUris = new HashSet<String>();
        walkTree(new TreeWalkNodeHandler() {
            @Override
            public boolean handleNode(XmlNode node) {
                /* Consider node itself */
                if (node instanceof uk.ac.ed.ph.jqtiplus.node.content.mathml.Math) {
                    elementNamespaceUris.add(QtiConstants.MATHML_NAMESPACE_URI);
                }
                else if (node instanceof ForeignElement) {
                    elementNamespaceUris.add(((ForeignElement) node).getNamespaceUri());
                }
                /* Now do attributes */
                for (Attribute<?> attribute : node.getAttributes()) {
                    if (attribute instanceof ForeignAttribute) {
                        attributeNamespaceUris.add(attribute.getNamespaceUri());
                    }
                }

                /* Keep descending */
                return true;
            }
        }, nodes);
        return new ForeignNamespaceSummary(elementNamespaceUris, attributeNamespaceUris);
    }

    public static void walkTree(TreeWalkNodeHandler handler, Iterable<? extends XmlNode> startNodes) {
        ConstraintUtilities.ensureNotNull(startNodes);
        ConstraintUtilities.ensureNotNull(handler);
        for (XmlNode startNode : startNodes) {
            doWalkTree(handler, startNode);
        }
    }

    public static void walkChildNodes(TreeWalkNodeHandler handler, Iterable<? extends XmlNode> startNodes) {
        ConstraintUtilities.ensureNotNull(startNodes);
        ConstraintUtilities.ensureNotNull(handler);
        for (XmlNode startNode : startNodes) {
            for (NodeGroup<?> nodeGroup : startNode.getNodeGroups()) {
                for (XmlNode childNode : nodeGroup.getChildren()) {
                    doWalkTree(handler, childNode);
                }
            }
        }
    }

    private static void doWalkTree(TreeWalkNodeHandler handler, XmlNode currentNode) {
        boolean descend = handler.handleNode(currentNode);
        if (descend) {
            for (NodeGroup<?> nodeGroup : currentNode.getNodeGroups()) {
                for (XmlNode childNode : nodeGroup.getChildren()) {
                    doWalkTree(handler, childNode);
                }
            }
        }
    }

}
