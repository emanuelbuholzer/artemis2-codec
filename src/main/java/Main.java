import org.apache.commons.cli.*;

public class Main {

    public static void printUsage() {
        System.out.println("Usage: java -jar artemis2-codec.jar <Options>\n");
        System.out.println("Options: ");
        System.out.println("  -e  --encode       encode the plain text secret");
        System.out.println("  -d  --decode       decode the hashed or masked secret");
        System.out.println("  -v  --verify       verify the hashed or masked secred with the plain text secret\n");
        System.out.println("  -p  --plain-secret plain text secret");
        System.out.println("  -m  --mask-secret  masked or hashed secret");
        System.out.println("  -r  --reversible   encode and decode secrets which are reversible\n");
        System.out.println("Example: ");
        System.out.println("java -jar artemis2-codec.jar -erp safe                  // Encode the reversible secret 'safe'");
        System.out.println("java -jar artemis2-codec.jar -d -p safe -m 1024:F54F... // Verify the irreversible secret 'safe'\n");
    }

    public static void main(String[] args) {

        // Create posix command line parser
        CommandLineParser parser = new PosixParser();

        // Create options
        Options options = new Options();
        options.addOption("m", "mask-secret", true, "masked or hashed secret");
        options.addOption("p", "plain-secret", true, "plain text secret");
        options.addOption("r", "reversible", false, "encode such that the secret can be decoded");
        options.addOption("e", "encode", false, "encode the plain text secret");
        options.addOption("d", "decode", false, "decode the masked or hashed secret");
        options.addOption("v", "verify", false, "verify the hashed or masked secret with the plain text secret");

        try {
            // Parse command line arguments
            CommandLine line = parser.parse(options, args);

            // Arguments
            boolean reversible = line.hasOption("reversible");
            String maskSecret = line.getOptionValue("mask-secret");
            String plainSecret = line.getOptionValue("plain-secret");

            // Execution strategies
            boolean encode = line.hasOption("encode");
            boolean decode = line.hasOption("decode");
            boolean verify = line.hasOption("verify");

            // Verify that only one execution strategy can be active
            boolean exec = false;
            for (boolean execStrategy : new boolean[] { encode, decode, verify }) {
                if (execStrategy && exec) {
                    throw new Exception("Cannot execute multiple actions!");
                } else if (execStrategy) {
                    exec = true;
                }
            }

            // Initialize codec
            Codec codec = new Codec(reversible);

            // Execute the given strategy
            if (encode) {
                System.out.println(codec.encode(plainSecret));
            } else if (decode) {
                System.out.println(codec.decode(maskSecret));
            } else if (verify) {
                System.out.println(codec.verify(plainSecret, maskSecret));
            } else {
                printUsage();
            }
        } catch (Exception e) {
            System.err.println("artemis2-codec: " + e.getMessage());
            System.err.println("                Use java -jar artemis2-codec to print the help!");
        }
    }
}