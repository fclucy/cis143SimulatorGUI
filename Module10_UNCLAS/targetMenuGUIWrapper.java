package Module10_UNCLAS;

public class targetMenuGUIWrapper {
    public static double getTargetDepthFromUser() {
        targetMenuGUI menu = new targetMenuGUI();
        while (menu.isDisplayable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return menu.getSelectedDepth();
    }
}