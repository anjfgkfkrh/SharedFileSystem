import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogInManager {
   private List<User> users;
   private ObjectMapper objectMapper;

   public LogInManager() {
      objectMapper = new ObjectMapper();
      loadUsersFromJsonFile("./users.json");
   }

   public User Login(String ID, String pass) {
      int n;
      for (User user : users) {
         n = user.logIn(ID, pass);
         if (n == 0) {
            return user;
         } else if (n == 2) {
            return null;
         }
      }
      return null;
   }

   public boolean signUp(String ID, String pass) {
      User user = new User(ID, pass);
      int n;
      for (int i = 0; i < users.size(); i++) {
         n = users.get(i).logIn(ID, pass);
         if (n == 2) {
            return false;
         }
      }
      File file = new File("./Files/" + ID);
      file.mkdir();
      user.setPath(file.getPath());
      users.add(user);
      saveUsersToJsonFile("./users.json");
      return true;
   }

   public void deleteID(String ID, String pass) {
      int n;
      for (int i = 0; i < users.size(); i++) {
         n = users.get(i).logIn(ID, pass);
         if (n == 2) {
            users.remove(i);
            break;
         }
      }
   }

   public void saveUsersToJsonFile(String filePath) {
      try {
         objectMapper.writeValue(new File(filePath), users);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void loadUsersFromJsonFile(String filePath) {
      try {
         File file = new File(filePath);
         if (file.length() == 0) { // 파일이 비어 있다면
            users = new ArrayList<User>(); // 새로운 User 객체 리스트 생성
         } else {
            users = objectMapper.readValue(file, new TypeReference<List<User>>() {
            });
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
