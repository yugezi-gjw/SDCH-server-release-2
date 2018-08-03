package com.varian.oiscn.security;

import com.varian.oiscn.security.util.EncryptionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Encryption Tool.<br>
 */
public class Main {

    protected static String operation = "Encryption";
    protected static boolean isEncryption = true;
    protected static String input = null;
    protected static String output = null;
    protected static String mode = "Simple";
    protected static boolean isSimple = true;

    public static void main(String[] args) {

        try {
            if (parseCommandArguments(args) < 0) {
                printUsage();
            } else {
                if (isEncryption) {
                    output = EncryptionUtil.encrypt(input);
                } else {
                    output = EncryptionUtil.decrypt(input);
                }
                printOutput();
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    protected static void printOutput() {
        if (isSimple) {
            System.out.print(output);
        } else {
            System.out.println("");
            System.out.println("Operation - " + operation);
            System.out.println("");
            if (isEncryption) {
                System.out.println("    ClearText:    " + input);
                System.out.println("    Encrypted:    " + output);
            } else {
                System.out.println("    Encrypted:    " + input);
                System.out.println("    Decrypted:    " + output);
            }
            System.out.println("");
        }
    }

    protected static void printUsage() {
        System.out.println("");
        System.out.println("Command: java -jar oiscn-security.jar [-operation <operation>] [-mode <Mode>] -text <text>");
        System.out.println("");
        System.out.println("parameters:");
        System.out.println("");
        System.out.println("     -operation <Operation>    optinal            Encryption (default) or decryption.");
        System.out.println("");
        System.out.println("     -mode <Mode>              optinal            Simple (default) or normal.");
        System.out.println("                                                  The simple mode would only output the result text.");
        System.out.println("");
        System.out.println("     -text <Text>              required           The text to encrypt or decrypt.");
        System.out.println("");
        System.out.println("     -h|help                   optional           Print this usage.");
        System.out.println("");
    }

    protected static int parseCommandArguments(String[] args) throws Exception {
        if (args == null || args.length < 1) {
            return -1;
        }

        final Map<String, String> map = new HashMap<String, String>();
        final int l = args.length;
        int i = 0;
        String key;
        while (i < l) {
            key = args[i];
            if (key == null || key.trim().length() == 0) {
                return 0;
            } else if (key.charAt(0) != '-' || key.length() < 2) {
                throw new Exception("parameter invalid");
            } else if (key.equalsIgnoreCase("-h") || key.equalsIgnoreCase("-help")) {
                return -1;
            }

            if (++i < l) {
                String val = args[i];
                if (val == null || val.trim().length() == 0) {
                    throw new Exception("parameter invalid");
                }
                map.put(key, val);
                i++;
            } else {
                throw new Exception("parameter invalid");
            }
        }

        if (map.containsKey("-operation")) {
            operation = map.remove("-operation");
            if (operation == null || operation.trim().length() == 0) {
                throw new Exception("Operation is empty");
            }
            if ("encryption".startsWith(operation.toLowerCase())) {
                isEncryption = true;
                operation = "Encryption";
            } else if ("decryption".startsWith(operation.toLowerCase())) {
                isEncryption = false;
                operation = "Decryption";
            } else {
                throw new Exception("Operation invalid");
            }
        }

        if (map.containsKey("-mode")) {
            mode = map.remove("-mode");
            if (mode == null || mode.trim().length() == 0) {
                throw new Exception("Mode is empty");
            }
            if ("simple".startsWith(mode.toLowerCase())) {
                isSimple = true;
                mode = "Simple";
            } else if ("normal".startsWith(mode.toLowerCase())) {
                isSimple = false;
                mode = "Normal";
            } else {
                throw new Exception("Mode invalid");
            }
        }

        if (map.containsKey("-text")) {
            input = map.remove("-text");
            if (input == null || input.trim().length() == 0) {
                throw new Exception("Text is empty");
            }
            if (input.length() > 128) {
                throw new Exception("Text is too long.");
            }
        } else {
            throw new Exception("Text is missing");
        }

        return 0;
    }
}
