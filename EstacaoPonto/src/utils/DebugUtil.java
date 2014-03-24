package utils;

/**
 * Created by Danilo on 24/03/14.
 */
public class DebugUtil{
        public static void printStackTrace(){
            printStackTrace(10, "");
        }

        public static void printStackTrace(int deep, String title){
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = 1; i<=deep; i++){
                System.out.println("----"+stackTrace[i]);
            }
        }
}