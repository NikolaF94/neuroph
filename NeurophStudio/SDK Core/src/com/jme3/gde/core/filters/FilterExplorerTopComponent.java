/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.core.filters;

import com.jme3.gde.core.assets.FilterDataObject;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
/*
@ConvertAsProperties(dtd = "-//com.jme3.gde.core.filters//FilterExplorer//EN",
autostore = false)
@TopComponent.Description(preferredID = "FilterExplorerTopComponent",
iconBase = "com/jme3/gde/core/filters/icons/eye.gif",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@ActionID(category = "Window", id = "com.jme3.gde.core.filters.FilterExplorerTopComponent")
@ActionReference(path = "Menu/Window" )
@TopComponent.OpenActionRegistration(displayName = "#CTL_FilterExplorerAction",
preferredID = "FilterExplorerTopComponent")*/
@SuppressWarnings("unchecked")

public final class FilterExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static final Logger logger = Logger.getLogger(FilterExplorerTopComponent.class.getName());
    private static FilterExplorerTopComponent instance;
    private static final String PREFERRED_ID = "FilterExplorerTopComponent";
    private transient ExplorerManager explorerManager = new ExplorerManager();
    private FilterDataObject currentFile;
    private FilterPostProcessorNode node;
    private boolean filterEnabled = false;

    public FilterExplorerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(FilterExplorerTopComponent.class, "CTL_FilterExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(FilterExplorerTopComponent.class, "HINT_FilterExplorerTopComponent"));
        ActionMap map = getActionMap();
        map.put("delete", ExplorerUtils.actionDelete(explorerManager, true));
        map.put("moveup", new MoveUpAction());
        map.put("movedown", new MoveDownAction());
        associateLookup(ExplorerUtils.createLookup(explorerManager, map));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 392, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 21, Short.MAX_VALUE));

        jToolBar1.add(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        closeFile();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("sdk.filters");
    }

    public void loadFile(FilterDataObject object) {
        currentFile = object;
        node = object.getLookup().lookup(FilterPostProcessorNode.class);
        explorerManager.setRootContext(node);
        setActivatedNodes(new Node[]{object.getNodeDelegate()});
        updateFilterState();
        open();
        requestVisible();
    }

    public void closeFile() {
        currentFile = null;
        node = null;
        explorerManager.setRootContext(Node.EMPTY);
        setActivatedNodes(new Node[]{});
        updateFilterState();
    }

    public synchronized void setFilterEnabled(final boolean enabled) {
        filterEnabled = enabled;
        updateFilterState();
    }

    private synchronized void updateFilterState() {
        final FilterPostProcessor fpp = this.node != null ? this.node.getFilterPostProcessor() : null;
        clearFilters();
        if (filterEnabled && fpp != null) {
            SceneApplication.getApplication().enqueue(new Callable() {
                public Object call() throws Exception {
                    SceneApplication.getApplication().getViewPort().addProcessor(fpp);
                    logger.log(Level.FINE, "Enabled post filters");
                    return null;
                }
            });
        }
    }

    private synchronized void clearFilters() {
        SceneApplication.getApplication().enqueue(new Callable() {
            public Object call() throws Exception {
                for (Iterator<SceneProcessor> it = SceneApplication.getApplication().getViewPort().getProcessors().iterator(); it.hasNext();) {
                    SceneProcessor proc = it.next();
                    if (proc instanceof FilterPostProcessor) {
                        it.remove();
                        proc.cleanup();
                    }
                    logger.log(Level.FINE, "Disabled post filters");
                }
                return null;
            }
        });
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link #findInstance}.
     */
    public static synchronized FilterExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new FilterExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the SceneExplorerTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized FilterExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(FilterExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FilterExplorerTopComponent) {
            return (FilterExplorerTopComponent) win;
        }
        Logger.getLogger(FilterExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
