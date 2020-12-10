package fbrs.utils;

import fbrs.model.Entry;
import fbrs.model.User;

import java.util.List;

public class FBRSPrintableUserEntry {
    private User user;
    private List<Entry> todaysEntries;
    private int arrearsCount;
    private int returnedToday;

    public FBRSPrintableUserEntry() {
    }

    public FBRSPrintableUserEntry(User user, List<Entry> todaysEntries, int arrearsCount, int returnedToday) {
        this.user = user;
        this.todaysEntries = todaysEntries;
        this.arrearsCount = arrearsCount;
        this.returnedToday = returnedToday;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Entry> getTodaysEntries() {
        return todaysEntries;
    }

    public void setTodaysEntries(List<Entry> todaysEntries) {
        this.todaysEntries = todaysEntries;
    }

    public int getArrearsCount() {
        return arrearsCount;
    }

    public void setArrearsCount(int arrearsCount) {
        this.arrearsCount = arrearsCount;
    }

    public boolean isUserDetailed() {
        return getUser().isSelected();
    }

    public void setDetailed(boolean detailed) {
        getUser().setSelected(detailed);
    }

    public int getReturnedToday() {
        return returnedToday;
    }

    public void setReturnedToday(int returnedToday) {
        this.returnedToday = returnedToday;
    }

    public int rowCount(boolean isReportDetailed) {
        return 3 + ((isReportDetailed || this.isUserDetailed()) ? todaysEntries.size() : 1);
    }

    public int todaysBuksaCount() {
        int count = 0;
        for (Entry entry : todaysEntries)
            if (entry.getTakerId() == user.getId())
                count += entry.getQuantity();
        return count;
    }

}