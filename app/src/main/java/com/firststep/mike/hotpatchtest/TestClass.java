package com.firststep.mike.hotpatchtest;

/**
 * Created by mike on 2016/8/6.
 */
public class TestClass {

    private String testString;

    public TestClass(){
        this.testString = "test string(originlal)";
    }
    public String getTestString() {
        return testString;
    }

    public String getChangedString() {
        return "test string(changed)";
    }
    public void setTestString(String string){
        this.testString = string;
    }

}
