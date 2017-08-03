package galaxy;

public class TestMain extends Director {

    public static void main(String[] args) {
        new TestMain();
    }

    public TestMain() {
        GameSettings.getVisualizer(this);
        //System.out.println(DefaultVisualizer.class);
        //System.out.println(GameSettingsReader.getDimensions());
    }
}
