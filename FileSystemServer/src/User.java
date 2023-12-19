public class User {
   private String ID;
   private String pass;
   private String path;

   public User(String ID, String pass) {
      this.ID = ID;
      this.pass = pass;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getPath() {
      return path;
   }

   // ID,Pass가 맞을시 0
   // ID가 틀릴 시 1
   // Pass가 틀릴시 2
   public int logIn(String ID, String pass) {
      if (!this.ID.equals(ID))
         return 1;
      if (!this.pass.equals(pass))
         return 2;
      return 0;
   }
}
