public class Task {
    private String title;
    private String details;
    private boolean inProgress;

    //getters and setters
    public String getTitle() {
        return title;
    }
    public String getDetails() {
         return details;
    }
    public boolean getInProgress() { return inProgress; }
    public void setTitle(String rename) {
        title = rename;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public void setInProgress() { inProgress = !inProgress; }

    public Task(String title) {
        this.title = title;
        this.inProgress = false;
        this.details = "No details set.";
    }
    // no arg constructor for Jackson
    public Task() { }
}
