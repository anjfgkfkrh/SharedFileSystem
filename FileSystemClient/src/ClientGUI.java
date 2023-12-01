import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientGUI extends JFrame {
   private FileTree fileTree;
   private MainPanel mainPanel;
   private ButtonPanel buttonPanel;
   private FileNode fileNode;
   private Client client;

   public ClientGUI() {

      client = new Client();

      addWindowListener((WindowListener) new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            client.close(); // 창이 닫힐 때 Client의 소켓을 닫음
         }
      });

      setTitle("SharedFileSystem");
      setSize(800, 630);
      setResizable(false);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(null);

      // 파일 구조 생성
      fileNode = jsonToFileNode();

      mainPanel = new MainPanel(fileNode);
      mainPanel.setBounds(200, 0, 600, 530);
      fileTree = new FileTree(mainPanel, fileNode);
      fileTree.setBounds(0, 0, 200, 530);
      buttonPanel = new ButtonPanel(mainPanel, client, fileTree);
      buttonPanel.setBounds(0, 530, 800, 70);

      add(fileTree);
      add(mainPanel);
      add(buttonPanel);

      setVisible(true);
   }

   private FileNode jsonToFileNode() {
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         FileNode fileNode = objectMapper.readValue(new File("./FileStructure.json"), FileNode.class);
         return fileNode;
      } catch (IOException e) {
         return null;
      }
   }

}

class FileTree extends JScrollPane {

   public FileTree(MainPanel mainPanel, FileNode fileNode) {

      // 트리 노드 생성
      DefaultMutableTreeNode rootTreeNode = createTreeNode(fileNode);
      // 트리 생성 및 트리 노드 부착
      JTree tree = new JTree(rootTreeNode);
      tree.setToggleClickCount(0);

      // 폴더 아이콘 렌더링
      DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {

         @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
               boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            FileNode fileNode = (FileNode) node.getUserObject();

            if (fileNode.isDirectory()) {
               setIcon(getOpenIcon());
            } else {
               setIcon(getLeafIcon());
            }

            return this;
         }
      };
      // 렌더러 부착
      tree.setCellRenderer(renderer);

      // 뷰포트에 트리 부착
      setViewportView(tree);

      tree.addMouseListener((MouseListener) new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
               DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

               if (selectedNode != null) {
                  FileNode fileNode = (FileNode) selectedNode.getUserObject();

                  if (fileNode.isDirectory()) {
                     mainPanel.displayDirectoryContents(fileNode);
                  }
               }
            }
         }
      });

      setVisible(true);
   }

   public DefaultMutableTreeNode createTreeNode(FileNode fileNode) {
      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(fileNode);

      if (fileNode.isDirectory()) {
         List<FileNode> childNodes = fileNode.getChilds();
         if (childNodes != null) {
            for (FileNode childNode : childNodes) {
               if (childNode.isDirectory())
                  treeNode.add(createTreeNode(childNode));
            }
         }
      }

      return treeNode;
   }

}

class MainPanel extends JScrollPane {
   private JList<FileNode> list;
   private DefaultListModel<FileNode> listModel;
   private FileNode currentDirectory;

   public MainPanel(FileNode fileNode) {
      listModel = new DefaultListModel<>();
      list = new JList<>(listModel);

      displayDirectoryContents(fileNode);

      list.setCellRenderer((ListCellRenderer<? super FileNode>) new ListCellRenderer<FileNode>() {
         @Override
         public Component getListCellRendererComponent(JList<? extends FileNode> list, FileNode value, int index,
               boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, value, index,
                  isSelected, cellHasFocus);
            if (value.isDirectory()) {
               label.setIcon(UIManager.getIcon("FileView.directoryIcon"));
            } else {
               label.setIcon(UIManager.getIcon("FileView.fileIcon"));
            }
            return label;
         }
      });

      list.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
               FileNode selectedNode = list.getSelectedValue();

               if (selectedNode != null) {
                  if (selectedNode.getName().equals("..")) {
                     displayDirectoryContents(selectedNode.getParent());
                  } else if (selectedNode.isDirectory()) {
                     displayDirectoryContents(selectedNode);
                  }
               }
            }
         }
      });

      setViewportView(list);
   }

   public void displayDirectoryContents(FileNode directory) {
      currentDirectory = directory;

      listModel.clear();

      if (currentDirectory.getParent() != null) {
         listModel.addElement(new FileNode("..", true, currentDirectory.getParent()));
      }

      for (FileNode childNode : currentDirectory.getChilds()) {
         listModel.addElement(childNode);
      }
   }

   public String getPath() {
      return currentDirectory.getPath();
   }
}

class ButtonPanel extends JPanel {
   public JButton upLoad;
   public JButton downLoad;
   public JButton delete;
   public JButton refresh;
   public JButton setDownloadLocation;

   public ButtonPanel(MainPanel mainPanel, Client client, FileTree fileTree) {

      setLayout(null);

      upLoad = new JButton("Upload");
      downLoad = new JButton("Download");
      delete = new JButton("Delete");
      refresh = new JButton("Refresh");
      setDownloadLocation = new JButton("Download Location");

      refresh.setBounds(0, 0, 200, 70);
      upLoad.setBounds(200, 0, 150, 70);
      downLoad.setBounds(350, 0, 150, 70);
      delete.setBounds(500, 0, 150, 70);
      setDownloadLocation.setBounds(650, 0, 150, 70);

      upLoad.addActionListener(e -> {
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 파일과 디렉토리 모두 선택 가능하게 설정
         int returnValue = fileChooser.showOpenDialog(null);
         if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String currentPath = mainPanel.getPath();

            client.fileOutputMode(selectedFile, currentPath);
            // 서버에 파일 경로와 현재 경로를 전송하는 코드 작성
         }
      });

      refresh.addActionListener(e -> {

      });

      add(refresh);
      add(upLoad);
      add(downLoad);
      add(delete);
      add(setDownloadLocation);
   }
}