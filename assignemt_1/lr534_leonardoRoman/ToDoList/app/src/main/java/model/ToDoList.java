package model;

public class ToDoList {
    private String task;
    private String description;
    public ToDoList(String task, String description){
        this.task = task;
        this.description = description;
    }
    public void setTask(String task){
        this.task = task;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getTask(){
        return task;
    }
    public String getDescription(){
        return description;
    }
    public String toString(){
        return task+"\n"+description;
    }
}
