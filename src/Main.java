public class Main {
    public static void main(String[] args) throws MemoryOverflowException, FileFormatException, NoSuchFileException, InvalidProcessException, StackOverflowException {
        ManagementInterfaceImpl m = new ManagementInterfaceImpl((short) 32);

        int id = m.loadProcessToMemory("teste.txt");
        int total = m.allocateMemoryToProcess(0, 130);
        System.out.println(m.getPageTable(0));
    }
}
