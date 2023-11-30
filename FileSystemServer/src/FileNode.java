import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "path")
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

   public void setParent(FileNode parent) {
      this.parent = parent;
   }

   public void SetIsDirectory(boolean b) {
      directory = b;
   }

   public List<FileNode> getChilds() {
      return childs;
   }

   public FileNode getParent() {
      return parent;
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
