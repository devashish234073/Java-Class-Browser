
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Color;

import java.awt.event.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class ClassBrowser extends Frame{

    private TextField tf;
    private TextArea ta;
    private Button btn;
    private ClassBrowser me = this;
    private HashMap<String,Object> classMap = new HashMap<String,Object>();

    public static void main(String[] args) {
        ClassBrowser ui=new ClassBrowser();
        ui.setSize(400,700);
        ui.setLayout(null);
        ui.initAllElements();
        ui.styleAllElements();
        ui.addAllListeners();
        ui.initClassMap();
        ui.addAllElements();
        ui.setVisible(true);
    }

    public void initAllElements() {
        tf  = new TextField();
        ta  = new TextArea();
        btn = new Button("Show available classes");
    }

    public void styleAllElements() {
        tf.setBackground(Color.BLACK);
        ta.setBackground(Color.BLACK);
        tf.setForeground(Color.WHITE);
        ta.setForeground(Color.WHITE);
        btn.setBounds(10,30,150,20);
         tf.setBounds(10,50,380,20);
         ta.setBounds(10,70,380,620);
        ta.setEditable(false);
    }

    public void addAllListeners() {
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        tf.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e){}
            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode() == 10){
                    ta.setText(me.getAllDetails(tf.getText()));
                } else {
                    ta.setText("");
                }
            }
            @Override
            public void keyTyped(KeyEvent e){}
        });
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                Iterator itr=classMap.keySet().iterator();
                String vals="";
                while(itr.hasNext()) {
                    vals += classMap.get(itr.next()).getClass().getName()+"\n";
                }
                ta.setText(vals);
            }
        });
    }

    private void addToClassMap(String identifier,Object o){
        classMap.put(identifier.toLowerCase(),o);
    }

    private String howToAddClass() {
        String howToAdd="\nDetailed Steps:";
        howToAdd += "\n-----------------------------------------------------------";
        howToAdd += "\n1.intialize an object of the 'class' that you want to add.";
        howToAdd += "\n2.put it into 'initClassMap' method.";
        howToAdd += "\n3.call 'addToClassMap' by passing below args:";
        howToAdd += "\n    a).'identifier' for the class.";
        howToAdd += "\n    b).'object that you created in step 1.";
        howToAdd += "\n4.compile and run again.";
        howToAdd += "\n-----------------------------------------------------------";
        return howToAdd;
    }

    private void initClassMap() {
        Integer i=0;
        addToClassMap("integer",i);
        Long l=0L;
        addToClassMap("long",l);
        String s="";
        addToClassMap("string",s);
        Character c='\0';
        addToClassMap("character",c);
        ArrayList al=new ArrayList();
        addToClassMap("arraylist",al);
        HashMap hm=new HashMap();
        addToClassMap("hashmap",hm);
        HashSet hs=new HashSet();
        addToClassMap("hashset",hs);
        Scanner sc=new Scanner(System.in);
        addToClassMap("scanner",sc);
        TextField tf=new TextField();
        addToClassMap("textfield",tf);
    }

    private String getInterfaceNames(Class c){
        String interfaceNames="";
        Class[] intrfc=c.getInterfaces();
        for(int indx=0;indx<intrfc.length;indx++){
            interfaceNames += intrfc[indx].getName();
            if(indx != intrfc.length-1){
                interfaceNames += ", ";
            }
        }
        return interfaceNames;
    }

    private String getBasicDetails(Class c) {
        String info="";
        info += "------------------------------------------------------\n";
        info += "Fullname\t:"+c.getName()+"\n";
        info += "Super-class\t:"+c.getSuperclass().getName()+"\n";
        info += "Interfaces\t:"+getInterfaceNames(c)+"\n";
        info += "------------------------------------------------------\n";
        return info;
    }

    private String getFieldDetails(Class c) {
        String fieldDetails="";
        Field[] f = c.getDeclaredFields();
        for(int indx=0;indx<f.length;indx++) {
            fieldDetails += f[indx]+"\n";
        }
        return fieldDetails;
    }

    private String getMethodDetails(Class c) {
        String methodDetails="";
        Method[] m = c.getDeclaredMethods();
        HashMap<String,String> methodMap=new HashMap<String,String>();
        for(int indx=0;indx<m.length;indx++) {
            String methodNameFull=m[indx]+"";
            methodNameFull=methodNameFull.replace(c.getName()+".","");
            String[] arr=methodNameFull.split(" ");
            String methodNamePrefStr="";
            String methodNameStr="";
            if(methodNameFull.indexOf(")")>-1 && methodNameFull.indexOf("(")>-1) {
                for(int i=0;i<arr.length;i++) {
                    if(!arr[i+1].contains("(") && !arr[i+1].contains(")")) {
                        methodNamePrefStr += arr[i]+" ";
                    } else {
                        for(int j=i;j<arr.length;j++) {
                            methodNameStr += arr[j]+" ";
                        }
                        break;
                    }
                }
            } else {
                methodNamePrefStr = "UN-CATEGORIZED";
                methodNameStr     = methodNameFull;
            }
            if(methodNamePrefStr.equals("")) {
                methodNamePrefStr = "No-Access specifier";
            }
            if(methodMap.containsKey(methodNamePrefStr)) {
                methodMap.put(methodNamePrefStr,methodMap.get(methodNamePrefStr)+"\n"+methodNameStr);
            } else {
                methodMap.put(methodNamePrefStr,methodNameStr);
            }
        }
        Iterator itr=methodMap.keySet().iterator();
        while(itr.hasNext()){
            String   key = (String)itr.next();
            String[] val = methodMap.get(key).split("\n");
            methodDetails += key + " ("+val.length+")\n";
            for(int i=0;i<val.length;i++) {
                methodDetails += "    "+val[i]+"\n";
            }
        }
        return methodDetails;
    }

    private String getAllDetails(String className){
        className=className.toLowerCase().trim();
        if(className.equals("")) {
            return "empty string can't be evaluated!";
        }
        Object o=classMap.get(className);
        if(o == null){
            return "'"+className+"' class not found in our database. \nPlease add and compile and run again!\n"+howToAddClass();
        }
        Class c=o.getClass();

        String basicDetails=getBasicDetails(c);
        String fieldDetails=getFieldDetails(c);
        String methodDetails=getMethodDetails(c);

        return basicDetails+"\n"+fieldDetails+"\n"+methodDetails;
    }

    public void addAllElements() {
        add(tf);
        add(ta);
        add(btn);
    }
}
