package application;

import processing.core.PApplet;

public class Main extends PApplet {
    
    public static void main(String[] args) {
        
        String className = "application.Main";
        
        try {
            PApplet.main(Class.forName(className));
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class " + className);
            e.printStackTrace();
        }
    }
    
    @Override
    public void settings() {
        size(600, 600);
    }
}
