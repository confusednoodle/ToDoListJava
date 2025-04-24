import java.util.ArrayList;
import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

// TODO: maybe replace all "back" options with the single Q
// TODO: maybe implement persistent path storage

public class TaskManager {
    private ArrayList<Task> incomplete = new ArrayList<Task>();
    private ArrayList<Task> archived = new ArrayList<Task>();
    private Scanner scanner;
    private String input = "";
    // the user can indicate a path they want to save in, the path isn't saved
    private FileManager fm;

    // upper menu input handling, this is the function main hands over to
    public void menu() {
        scanner = new Scanner(System.in);
        fm = new FileManager();


        while(!(input.equals("1")||input.equals("2")||input.equals("3")||input.equals("4")||input.equals("5")||input.equals("6"))) {
            do {
                int wips = incomplete.size();
                System.out.println(ansi().fg(Ansi.Color.BLUE).a("Welcome to your ToDo-List! You have "+wips+" incomplete task(s).\n" +
                        "Input B in any menu to return to the previous menu.").reset() +
                        "\n1) List tasks\n" +
                        "2) Add task\n" +
                        "3) Clear tasks\n" +
                        "4) Save to file\n" +
                        "5) Load from file\n" +
                        "6) Specify custom file path");
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
                        ArrayList<ArrayList<Task>> arrays = fm.read();
                        incomplete = arrays.get(0);
                        archived = arrays.get(1);
                        break;

                    case "6":
                        fm.changePath(scanner);
                        break;

                    case "B":
                    case "b":
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
                        "2) Archived tasks");
                input = scanner.next();

                switch (input) {
                    case "1":
                        taskList = incomplete;
                        System.out.println("Selected incomplete tasks.");
                        break;

                    case "2":
                        taskList = archived;
                        System.out.println("Selected archived tasks.");
                        break;

                    case "b":
                    case "B":
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
                    Ansi status = ansi().fg(Ansi.Color.YELLOW).a("WORK IN PROGRESS").reset();
                    System.out.println("" + i + ". " + taskList.get(i).getTitle() + ": "+status+"\n");
                } else {
                    System.out.println("" + i + ". " + taskList.get(i).getTitle() + "\n");
                }
            }
            System.out.println("Select a task.");
            while (true) {
                int num;
                String taskNr;
                taskNr = scanner.nextLine();
                if (taskNr.equals("b")||taskNr.equals("B")) {
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
        Ansi status;

        // options for incomplete tasks
        if (taskList == incomplete) {
            while (!(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5") || input.equals("6") || input.equals("7"))) {
                // determine the status of the task
                if (task.getInProgress()) {
                    status = ansi().fg(Ansi.Color.YELLOW).a("WORK IN PROGRESS").reset();
                } else {
                    status = ansi().fg(Ansi.Color.RED).a("INCOMPLETE").reset();
                }
                System.out.println("Task " + i + ") " + taskList.get(i).getTitle() + "; status: " + status + "\n" +
                        "1) Get details\n" +
                        "2) Set details\n" +
                        "3) Change progress status\n" +
                        "4) Mark as done\n" +
                        "5) Delete\n" +
                        "6) Assign priority");
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

                    case "b":
                    case "B":
                        return;
                }
            }
        } else { // options for complete tasks
            while (!(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4"))) {
                // only one possible status for archived tasks
                status = ansi().fg(Ansi.Color.GREEN).a("DONE").reset();
                System.out.println("Task " + i + ") " + taskList.get(i).getTitle() + "; status: " + status + "\n" +
                        "1) Get details\n" +
                        "2) Set details\n" +
                        "3) Delete");
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

                    case "b":
                    case "B":
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
                "2) Archived");
        input = scanner.next();

        switch (input) {
            case "1":
                incomplete.clear();
                System.out.println("Cleared all incomplete tasks.");
                break;

            case "2":
                archived.clear();
                System.out.println("Cleared all archived tasks.");
                break;

            case "b":
            case "B":
                return;
        }
    }

    public TaskManager() { }
}
