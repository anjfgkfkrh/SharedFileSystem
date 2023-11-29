public class FilePrinter {
   public static void print(FileNode fileNode) {
      System.out.println(fileNode.getName());
      if (fileNode.isDirectory()) {
         for (FileNode child : fileNode.getChilds()) {
            print(child);
         }
      }
   }
}