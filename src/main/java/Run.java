public class Run {

    String fontWeight = "LIGHT";
    String font = "FMABHAYA";
    int fontSize = 12;
    private String text = "";
    float lastCharStartX = 0;
    float lastCharStartY = 0;
    float lastCharEndX = 0;
    float lastCharEndY = 0;
    boolean pageBreak=false;

    public void setText(String text, float startX, float startY, float endX, float endY,Paragraph paragraph){

        float lastCharMidX = (this.lastCharStartX + this.lastCharEndX)/2;
        float currentCharMidX = (startX+endX)/2;
        if (startY <70){
//            System.out.println("Abdominal pain");
            return;
        }
        else if (this.text==""){
            Run lastlastRun = paragraph.getLastLastLastRun();
            Run lastRun = paragraph.getLastLastRun();
            if (text.equals("%")){
            }
            if (lastlastRun != null){
                float lastCharMidXL = (lastRun.lastCharStartX + lastRun.lastCharEndX)/2;
                System.out.println("Not null run returned lastLastX: "+lastCharMidXL + "  currentX : "+currentCharMidX);

                if (lastCharMidXL > currentCharMidX && (Float.compare(lastRun.lastCharStartY,startY)==0)){
                    String adjustedText = lastlastRun.getText()+text;
                    lastlastRun.setextRaw(adjustedText);
                    System.out.println("Position rechanged in a run change: "+lastlastRun.getText() + " lastX :"+lastCharMidXL + "new midX : "+currentCharMidX);
                    return;
                }
            }

        }

        if (lastCharMidX > currentCharMidX && (Float.compare(lastCharStartY,startY)==0)){
            this.text = insertString(this.text, text, this.text.length()-2);
            System.out.println("Position rechanged : "+this.text + " lastX :"+lastCharMidX + "new midX : "+currentCharMidX);
        }
        else if (Float.compare(lastCharStartY,startY)<0 && lastCharStartY!=0){
            this.text += " " +text;         // New line without space
        }
//        else if (text.equals(".") && Float.compare(lastCharEndX,endX)>0){
//            this.text = insertString(this.text, text, this.text.length()-2);
//            System.out.println("Position rechanged : "+this.text + " lastX :"+lastCharMidX + "new midX : "+currentCharMidX);
//
//        }
        else {
            this.text += text;
        }

        lastCharStartX = startX;
        lastCharStartY = startY;
        lastCharEndY = endY;
        lastCharEndX = endX;
    }

    private String insertString(
            String originalString,
            String stringToBeInserted,
            int index)
    {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        return newString;
    }

    public String getText(){
        return this.text;
    }

    public void addSpace(){
        this.text += " ";
    }

    public void setextRaw(String text) {
        this.text = text;
    }
}
