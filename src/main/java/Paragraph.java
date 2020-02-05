import java.util.ArrayList;

public class Paragraph {


    ArrayList<Run> textRuns = new ArrayList<Run>();
    String column = "LEFT";     //LEFT , RIGHT
    public void addTextRun(Run textRun){
        textRuns.add(textRun);
    }

    public Run getRun(float startX, float startY, float endX, float endY, String fontFamiliy, int fontSize, String fontWeight, String text){

        Run lastRun;
        Run newRun = new Run();
        boolean changed = false;
        if (textRuns.size() !=0){
            lastRun = textRuns.get(textRuns.size()-1);
        }
        else {
//            System.out.println("index");
            lastRun = newRun;
            changed = true;
        }

        if (!fontFamiliy.equals(lastRun.font)){
//            System.out.println("font : " + fontFamiliy);
            newRun.font = fontFamiliy;
            lastRun = newRun;
            changed = true;

        }
        if (fontSize != lastRun.fontSize){
//            System.out.println("fontSize : " + fontSize);
            newRun.fontSize = fontSize;
            lastRun = newRun;
            changed = true;

        }
        if (!fontWeight.equals(lastRun.fontWeight)){
//            System.out.println("fontWeight : "+fontWeight);
            newRun.fontWeight = fontWeight;
            lastRun = newRun;
            changed = true;

        }

        if (changed){
            textRuns.add(newRun);
        }
        lastRun.setText(text,startX,startY,endX,endY,this);
        return lastRun;

    }

    public Run getLastRun(){

        Run lastRun;
        Run newRun = new Run();
//        boolean changed = false;
//        System.out.println(textRuns.size());
        if (textRuns.size() !=0){
            lastRun = textRuns.get(textRuns.size()-1);
        }
        else {
            lastRun = newRun;
        }
        return lastRun;
    }

    public Run getLastLastLastRun(){

        Run lastRun;
//        System.out.println(textRuns.size());
        if (textRuns.size() >2){
            lastRun = textRuns.get(textRuns.size()-3);
        }
        else {
            lastRun = null;
        }
        return lastRun;
    }
    public Run getLastLastRun(){

        Run lastRun;
//        System.out.println(textRuns.size());
        if (textRuns.size() >1){
            lastRun = textRuns.get(textRuns.size()-2);
        }
        else {
            lastRun = null;
        }
        return lastRun;
    }
}
