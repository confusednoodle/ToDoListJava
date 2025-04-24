import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: maybe replace all "back" options with the single Q
// TODO: maybe implement persistent path storage

public class TaskManager {
    private ArrayList<Task> incomplete = new ArrayList<Task>();
    private ArrayList<Task> archived = new ArrayList<Task>();
    private Scanner scanner;
    private String input = "";
    // the user can indicate a path they want to save in, the path isn't saved
    // FIXME uncomment if breaks: private String path = "";
    private FileManager fm;

    // upper menu input handling, this is the function main hands over to
    public void menu() {
        scanner = new Scanner(System.in);
        fm = new FileManager();


        while(!(input.equals("1")||input.equals("2")||input.equals("3")||input.equals("4")||input.equals("5")||input.equals("6"))) {
            do {
                int wips = incomplete.size();
                System.out.println("Welcome to your ToDo-List! You have "+wips+" incomplete task(s).\n" +
                        "1) List tasks\n" +
                        "2) Add task\n" +
                        "3) Clear tasks\n" +
                        "4) Save to file\n" +
                        "5) Load from file\n" +
                        "6) Specify custom file path\n" +
                        "7) Exit");
                input = scanner.next();

                switch (input) {
                    case "1":
                        listHandling();
                        break;

                    case "2":
                        System.out.println("Enter the name of your task:");
                        // necessary duplication to read empty line from next
                        input = scanner.nextLine();
                        input = scanner.nextLine();
                        newTask(input);
                        System.out.println("Added new task.");
                        break;

                    case "3":
                        clearTasks();
                        break;

                    case "4":
                        fm.save(incomplete,archived);
                        break;

                    case "5":
                        fm.read(incomplete,archived);
                        break;

                    case "6":
                        fm.changePath(scanner);
                        break;

                    case "7":
                        return;
                }
            } while (true);
        }
    }
    // task list selector
    public void listHandling() {
        while (true){
            input = "";
            ArrayList<Task> taskList = new ArrayList<Task>();

            while (!(input.equals("1") || input.equals("2"))) {
                System.out.println("List which tasks?\n" +
                        "1) Incomplete tasks\n" +
                        "2) Archived tasks\n" +
                        "3) Back");
                input = scanner.next();
                if (input.equals("1")) {
                    taskList = incomplete;
                    System.out.println("Selected incomplete tasks.");
                } else if (input.equals("2")) {
                    taskList = archived;
                } else if (input.equals("3")) {
                    return;
                }
            }
            list(taskList);
        }
    }
    // lists all tasks of the specified array list, manages task selection
    public void list(ArrayList<Task> taskList) {
        while (true){
            input = "";
            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i).getInProgress()) {
                    // display in progress status for tasks in progress
                    System.out.println("" + i + ". " + taskList.get(i).getTitle() + ": IN PROGRESS \n");
                } else {
                    System.out.println("" + i + ". " + taskList.get(i).getTitle() + "\n");
                }
            }
            System.out.println("Select a task or go back with Q+ENTER.");
            while (true) {
                int num;
                String taskNr;
                taskNr = scanner.nextLine();
                if (taskNr.equals("q")||taskNr.equals("Q")) {
                    return;
                } else {
                    // gets value if input is a valid number
                    try {
                        num = Integer.parseInt(taskNr);
                        // if num is within scope, call task management function
                        if (num >= 0 && num < taskList.size()) {
                            manageTask(taskList, num);
                            break;
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }
        }
    }
    // manages specific task information and task options
    public void manageTask(ArrayList<Task> taskList, int i) {
        input = "";
        Task task = taskList.get(i);
        String status;
        // determine the status of the task
        if (taskList == incomplete) {
            if (task.getInProgress()) {
                status = "WORK IN PROGRESS";
            } else {
                status = "INCOMPLETE";
            }
        } else {
            status = "DONE";
        }
        // options for incomplete tasks
        if (taskList == incomplete) {
            while (!(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5") || input.equals("6") || input.equals("7"))) {
                System.out.println("Task " + i + ") " + taskList.get(i).getTitle() + "; status: " + status + "\n" +
                        "1) Get details\n" +
                        "2) Set details\n" +
                        "3) Change progress status\n" +
                        "4) Mark as done\n" +
                        "5) Delete\n" +
                        "6) Assign priority\n" +
                        "7) Back");
                input = scanner.next();

                switch (input) {
                    case "1":
                        System.out.println(task.getDetails());
                        // use up empty input line from ENTER so that we remain in task management
                        input = scanner.nextLine();
                        break;

                    case "2":
                        System.out.println("Indicate task details:");
                        input = scanner.nextLine();
                        input = scanner.nextLine();
                        task.setDetails(input);
                        System.out.println("Details set.");
                        break;

                    case "3":
                        toggleWIP(i);
                        System.out.println("Changed progress status.");
                        input = scanner.nextLine();
                        break;

                    case "4":
                        markDone(i);
                        System.out.println("Marked as done.");
                        // don't consume empty input and let it go back to task list because task has been shifted
                        break;

                    case "5":
                        deleteTask(taskList, i);
                        System.out.println("Task deleted.");
                        // same for empty input as mark done
                        break;

                    case "6":
                        System.out.println("Indicate new priority:");
                        int pos;
                        input = scanner.nextLine();
                        input = scanner.nextLine();
                        try {
                            pos = Integer.parseInt(input);
                            if (pos >= 0 && pos < taskList.size()) {
                                assignPriority(i, pos);
                                System.out.println("Changed priority.");
                            }
                        } catch (NumberFormatException ex) {
                        }
                        // also go back to task list because task priority and thus index has now changed
                        break;

                    case "7":
                        return;
                }
            }
        } else { // options for complete tasks
            while (!(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4"))) {
                System.out.println("Task " + i + ") " + taskList.get(i).getTitle() + "; status: " + status + "\n" +
                        "1) Get details\n" +
                        "2) Set details\n" +
                        "3) Delete\n" +
                        "4) Back");
                input = scanner.next();

                switch (input) {
                    case "1":
                        System.out.println(task.getDetails());
                        // use up empty input line from ENTER so that we remain in task management
                        input = scanner.nextLine();
                        break;

                    case "2":
                        System.out.println("Indicate task details:");
                        input = scanner.nextLine();
                        input = scanner.nextLine();
                        task.setDetails(input);
                        System.out.println("Details set.");
                        break;

                    case "3":
                        deleteTask(taskList, i);
                        System.out.println("Task deleted.");

                    case "4":
                        return;
                }
            }
        }
    }

    public void newTask(String title) {
        incomplete.add(new Task(title));
    }
    public void toggleWIP(int i) {
        if (i>=0 && i<incomplete.size()) {
            incomplete.get(i).setInProgress();
        }
    }
    // removes a task from incomplete and transfers it to archived
    public void markDone(int i) {
        int size = incomplete.size();
        if (i>=0 && i<size) {
            // if the task was in Progress, set to no longer in progress
            if (incomplete.get(i).getInProgress()) { incomplete.get(i).setInProgress();}
            archived.add(incomplete.get(i));
            incomplete.remove(i);
        }
    }
    // moves the task at position i in incomplete to position p
    // if the spot is taken everything is shifted down
    public void assignPriority(int element, int priority) {
        if (priority>=incomplete.size()) {
            incomplete.add(incomplete.get(element));
            incomplete.remove(element);
        } else {
            Task temp = incomplete.get(element);
            incomplete.remove(element);
            incomplete.add(priority,temp);
        }
    }
    public void deleteTask(ArrayList<Task> list, int i) {
        list.remove(i);
    }
    public void clearTasks() {
        System.out.println("Clear which tasks?\n" +
                "1) Incomplete\n" +
                "2) Archived\n" +
                "3) Back");
        input = scanner.next();
        if (input.equals("1")) {
            incomplete.clear();
            System.out.println("Cleared all incomplete tasks.");
        } else if (input.equals("2")) {
            archived.clear();
            System.out.println("Cleared all archived tasks.");
        } else if (input.equals("3")) {return;}
    }

    // save ArrayLists to JSON
    // FIXME uncomment if breaks
    /*public void save() {
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
    }*/
    // read ArrayLists from JSON file
    // FIXME comment if breaks
    /*public void read() {
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
    }*/
    // FIXME uncomment if breaks
    /*public void changePath() {
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
    }*/
    public TaskManager() { }
}
