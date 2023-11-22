import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileStructure implements Serializable {
    private List<FileString> files;
    private List<FileStructure> directories;
    private String path;
    private String name;
    private int depth;

    public FileStructure(String path, String name, int depth) {

        this.path = path;
        this.name = name;
        this.depth = depth;

        files = new ArrayList<>();
        directories = new ArrayList<>();

        File dir = new File(path);

        if (dir.exists()) {
            File[] entries = dir.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isFile()) {
                        files.add(new FileString(entry.getName(), entry.getPath()));
                    } else if (entry.isDirectory()) {
                        directories.add(new FileStructure(entry.getPath(), entry.getName(), depth + 1));
                    }
                }
            }
        } else {
            System.out.println("폴더가 존재하지 않습니다.");
        }
    }

    public void refresh() {
        File dir = new File(path);

        files = new ArrayList<>();
        directories = new ArrayList<>();

        if (dir.exists()) {
            File[] entries = dir.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isFile()) {
                        files.add(new FileString(entry.getName(), entry.getPath()));
                    } else if (entry.isDirectory()) {
                        directories.add(new FileStructure(entry.getPath(), entry.getName(), depth + 1));
                    }
                }
            }
        } else {
            System.out.println("폴더가 존재하지 않습니다.");
        }
    }

    public void printDir() {
        for (FileStructure dir : directories) {
            for (int i = 0; i < depth; i++) {
                System.out.print("|  ");
            }
            if (depth != 0)
                System.out.print("ㄴ");
            System.out.println(dir.getName());
            dir.printDir();
        }
        for (FileString file : files) {
            for (int i = 0; i < depth; i++) {
                System.out.print("|  ");
            }
            if (depth != 0)
                System.out.print("ㄴ");
            System.out.println(file.getName());
        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

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
}
