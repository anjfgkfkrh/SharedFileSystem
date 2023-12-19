import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginGUI extends JFrame {
   private JTextField idField;
   private JPasswordField passwordField;
   private JButton loginButton;
   private JButton signUpButton;
   private Client client;

   public LoginGUI() {
      client = new Client();

      setTitle("로그인");
      setSize(300, 200);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Container contentPane = getContentPane();
      contentPane.setLayout(new FlowLayout());

      idField = new JTextField(20);
      passwordField = new JPasswordField(20);
      loginButton = new JButton("로그인");
      signUpButton = new JButton("회원가입");

      idField = new JTextField(20);
      passwordField = new JPasswordField(20);
      loginButton = new JButton("로그인");

      loginButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            String pass = new String(passwordField.getPassword());

            boolean success = client.logIn(id, pass);
            if (success) {
               setVisible(false);
               ClientGUI clientGUI = new ClientGUI(client);
            } else {
               JOptionPane.showMessageDialog(null, "로그인 실패!");
            }
         }
      });

      signUpButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            String pass = new String(passwordField.getPassword());

            boolean success = client.signUp(id, pass);
            if (success) {
               JOptionPane.showMessageDialog(null, "회원가입에 성공하였습니다.");
            } else {
               JOptionPane.showMessageDialog(null, "회원가입에 실패하였습니다.");
            }
         }
      });

      contentPane.add(new JLabel("ID:"));
      contentPane.add(idField);
      contentPane.add(new JLabel("비밀번호:"));
      contentPane.add(passwordField);
      contentPane.add(loginButton);
      contentPane.add(signUpButton);
      setVisible(true);
   }

}
