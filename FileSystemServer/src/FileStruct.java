import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileStruct implements Serializable {
    private List<File> files;
    private List<FileStruct> directories;
    private String path;
    private String name;
    private int depth;

    public FileStruct(String path, String name, int depth){

        this.path = path;
        this.name = name;
        this.depth = depth;

        files = new ArrayList<>();
        directories =  new ArrayList<>();

        File dir = new File(path);

        if (dir.exists()) {
            File[] entries = dir.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isFile()) {
                        files.add(entry);
                    } else if (entry.isDirectory()) {
                        directories.add(new FileStruct(entry.getPath(), entry.getName() , depth + 1));
                    }
                }
            }
        } else {
            System.out.println("폴더가 존재하지 않습니다.");
        }
    }

    public void refresh(){
        File dir = new File(path);

        files.clear();
        directories.clear();

        if (dir.exists()) {
            File[] entries = dir.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isFile()) {
                        files.add(entry);
                    } else if (entry.isDirectory()) {
                        directories.add(new FileStruct(entry.getPath(), entry.getName() , depth + 1));
                    }
                }
            }
        } else {
            System.out.println("폴더가 존재하지 않습니다.");
        }
    }
    public void printDir(){
        for(FileStruct dir : directories){
            for(int i=0; i<depth; i++) {
                System.out.print("|  ");
            }
            if(depth != 0)
                System.out.print("ㄴ");
            System.out.println(dir.getName());
            dir.printDir();
        }
        for(File file : files){
            for(int i=0; i<depth; i++) {
                System.out.print("|  ");
            }
            if(depth != 0)
                System.out.print("ㄴ");
            System.out.println(file.getName());
        }
    }

    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }
}
