package wrappers;

import entitys.Task;

public class TaskWrapper {
    private String id;
    private String dateTimeOpening;
    private String type;
    private String client;
    private String manager;
    private String closeDate;

    public TaskWrapper(Task task) {
       this.id = ""+task.getId();
       this.dateTimeOpening = task.getDateTimeOpening().toString();
       this.type = task.getType();
       this.client = task.getClient().getPersonalData().getUserNameTelegram();
       this.manager = task.getManager()==null?"@null":task.getManager().getPersonalData().getUserNameTelegram();
       this.closeDate = task.getDateTimeEnding()==null?"":task.getDateTimeEnding().toString();
    }

    public String getId() {
        return id;
    }

    public String getDateTimeOpening() {
        return dateTimeOpening;
    }

    public String getType() {
        return type;
    }

    public String getClient() {
        return client;
    }

    public String getManager() {
        return manager;
    }

    public String getCloseDate() {
        return closeDate;
    }
}
