import java.util.List;

public class FileNode {
   private String name;
   private String path;
   private boolean directory;
   private List<FileNode> childs;
   private FileNode parent;

   public void setName(String name) {
      this.name = name;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public void setChilds(List<FileNode> childs) {
      this.childs = childs;
   }

   public void SetIsDirectory(boolean b) {
      directory = b;
   }

   public List<FileNode> getChilds() {
      return childs;
   }

   public String getName() {
      return name;
   }

   public String getPath() {
      return path;
   }

   public boolean isDirectory() {
      return directory;
   }
}
