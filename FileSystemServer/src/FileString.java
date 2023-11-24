import java.io.Serializable;

class FileString implements Serializable {
   private String name;
   private String path;

   public FileString(String name, String path) {
      this.name = name;
      this.path = path;
   }

   public String getName() {
      return this.name;
   }

   public String getPath() {
      return this.path;
   }
}