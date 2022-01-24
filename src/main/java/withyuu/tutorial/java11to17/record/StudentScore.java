package withyuu.tutorial.java11to17.record;

public record StudentScore(String name, double score) {
    @Override
    public String toString() {
        return "%s gets %.2f score!".formatted(name, score);
    }
}
