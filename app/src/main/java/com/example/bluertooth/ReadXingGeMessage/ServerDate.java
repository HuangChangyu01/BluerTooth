package com.example.bluertooth.ReadXingGeMessage;

public class ServerDate {
    private static String  id;
    private static String op;
    private static String u_register;
    private static String RegistrationID;

    public static String getRegistrationID() {
        if(RegistrationID!=null){
            return RegistrationID;
        }
        return null;

    }

    public static void setRegistrationID(String registrationID) {
        RegistrationID = registrationID;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        ServerDate.id = id;
    }

    public static String getOp() {
        return op;
    }

    public static void setOp(String op) {
        ServerDate.op = op;
    }

    public static String getU_register() {
        return u_register;
    }

    public static void setU_register(String u_register) {
        ServerDate.u_register = u_register;
    }

    //配合用读取线程的方式
//    public static String getXinGeMessage() {
//        if(XinGeMessage!=null){
//            return XinGeMessage;
//        }
//        return null;
//
//    }
//
//    public static void setXinGeMessage(String xinGeMessage) {
//        XinGeMessage = xinGeMessage;
//    }
}
