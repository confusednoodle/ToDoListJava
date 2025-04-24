import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    private String path = "";
    private String input = "";

    // save the Array lists to a JSON file
    public void save(ArrayList<Task> incomplete, ArrayList<Task> archived) {
        // get the user's home directory as default
        String userHome = System.getProperty("user.home");
        String documentsPath;
        if (path.equals("")) {
            documentsPath = userHome + "/Documents";
        } else { // if the user has provided a custom path
            documentsPath = path + "/Documents";
        }

        File ToDoTasks = new File(documentsPath,"ToDoTasks");
        // create directory to save JSON files into if it doesn't exist yet
        if(!ToDoTasks.exists()) {
            ToDoTasks.mkdirs();
        }
        String toDoPath = documentsPath + "/ToDoTasks";

        ObjectMapper mapper = new ObjectMapper();

        File wip = new File(toDoPath,"incompleteTasks.json");
        File arch = new File(toDoPath,"archivedTasks.json");
        try {
            mapper.writeValue(wip,incomplete);
            System.out.println("Incomplete Tasks saved to file.");
            mapper.writeValue(arch,archived);
            System.out.println("Archived Tasks saved to file.");
        } catch (IOException e) {
            System.out.println("Failed to save files: "+e.getMessage());
        }
    }

    public void read(ArrayList<Task> incomplete, ArrayList<Task> archived) {
        // get the user's home directory as default
        String userHome = System.getProperty("user.home");
        String documentsPath;
        if (path.equals("")) {
            documentsPath = userHome + "/Documents";
        } else { // if the user has provided a custom path
            documentsPath = path + "/Documents";
        }
        // path and file prep
        String toDoPath = documentsPath + "/ToDoTasks";
        String incPath = toDoPath+"/incompleteTasks.json";
        String archPath = toDoPath+"/archivedTasks.json";
        ObjectMapper mapper = new ObjectMapper();
        File inc = new File(incPath);
        File arch = new File(archPath);

        // try to read from the JSON files
        try {
            incomplete = mapper.readValue(inc, new TypeReference<ArrayList<Task>>() {});
            archived = mapper.readValue(arch, new TypeReference<ArrayList<Task>>() {});
        } catch (IOException e) {
            System.out.println("Failed to read files: "+e.getMessage());
        }
    }

    public void changePath(Scanner scanner) {
        String tempPath;
        if (path.equals("")) {
            tempPath = "user/Documents";
        } else {
            tempPath = path;
        }
        System.out.println("Your current path is: "+tempPath);
        System.out.println("Indicate a valid absolute path to save the JSON files in.\n" +
                "The path will not be validated or persistently stored.\n" +
                "Type \"default\" to revert to the standard path.");
        // double to allow for actual input
        input = scanner.nextLine();
        input = scanner.nextLine();
        if (input.equals("default")) {
            path = "";
            System.out.println("Reverted to default path.");
            return;
        } else if (input.equals("q")||input.equals("Q")) {
            return;
        }
        path = input;
        System.out.println("Custom path set to: "+path);
    }

    public FileManager() {

    }
}
