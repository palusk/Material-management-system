package project.client;

public class ComboBoxItem {
    private String displayedValue;
    private String hiddenValue;

    public ComboBoxItem(String displayedValue, String hiddenValue) {
        this.displayedValue = displayedValue;
        this.hiddenValue = hiddenValue;
    }

    public String getDisplayedValue() {
        return displayedValue;
    }

    public String getHiddenValue() {
        return hiddenValue;
    }

    @Override
    public String toString() {
        return displayedValue;
    }
}
