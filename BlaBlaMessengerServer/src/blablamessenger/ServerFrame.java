package blablamessenger;

public class ServerFrame extends javax.swing.JFrame {
    Server server;
    
    public ServerFrame() {
        initComponents();
        Off.setEnabled( false );
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        On = new java.awt.Button();
        Off = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("ServerFrame"); // NOI18N

        On.setActionCommand("On");
        On.setLabel("Вкл.");
        On.setName("On"); // NOI18N
        On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnActionPerformed(evt);
            }
        });

        Off.setActionCommand("Off");
        Off.setLabel("Выкл.");
        Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OffActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(On, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Off, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(On, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Off, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        On.getAccessibleContext().setAccessibleName("On");
        Off.getAccessibleContext().setAccessibleName("Off");

        getAccessibleContext().setAccessibleName("ServerFrame");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnActionPerformed
        On.setEnabled( false );
        Off.setEnabled( true );
        server = new Server();
        server.start();
    }//GEN-LAST:event_OnActionPerformed

    private void OffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OffActionPerformed
        Off.setEnabled( false );
        On.setEnabled( true );
        server.interrupt();
    }//GEN-LAST:event_OffActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new ServerFrame().setVisible( true );
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button Off;
    private java.awt.Button On;
    // End of variables declaration//GEN-END:variables
}
