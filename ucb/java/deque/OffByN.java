public class OffByN implements CharacterComparator {
    private int n;

    public OffByN(int curr) {
        n = curr;
    }

    @Override
    public boolean equalChars(char x, char y) {
        if (Math.abs(x - y) == n) {
            return true;
        }
        return false;
    }
}
