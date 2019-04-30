import java.util.Iterator;
import java.util.LinkedList;

public class Classes {

    private String className;
    private String classDescription;
    private LinkedList<Integer> classNums;
    private LinkedList<String> classTimes;
    private LinkedList<String> professors;

    public Classes(String name, String description) {
        className = name;
        classDescription = description;

        classNums = new LinkedList<>();
        classTimes = new LinkedList<>();
        professors = new LinkedList<>();
    }

    public void addSection(Integer num, String time, String professor) {
        classNums.add(num);
        classTimes.add(time);
        professors.add(professor);
    }



    public boolean checkClassNumber(int check){
        for(Integer num: classNums)
            if(num.intValue() == check)
                return true;
        return false;
    }

    public String getHeader() {
        return className + "  -  " + classDescription;
    }

    public String getBody(int section) {
        int pos = classNums.indexOf(section);
        StringBuilder str = new StringBuilder();

        str.append(String.format("%-10s", "Section") + String.format("%-25s", "Time") + String.format("%-25s", "Professor") +"\n\n");
        str.append(String.format("%-10s", classNums.get(pos).toString()) + String.format("%-25s", classTimes.get(pos)) +
                String.format("%-25s", professors.get(pos)) +"\n");
        return str.toString();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(className + "  -  " + classDescription + "\n\n");
        str.append(String.format("%-25s", "Section") + String.format("%-25s", "Time") + String.format("%-25s", "Professor") +"\n\n");

        Iterator<Integer> nums = classNums.iterator();
        Iterator<String> times = classTimes.iterator();
        Iterator<String> names = professors.iterator();

        while(nums.hasNext()) {
            str.append(String.format("%-25s", nums.next().toString()) + String.format("%-25s", times.next()) +
                    String.format("%-25s", names.next()) +"\n");
        }

        return str.toString();
    }
}