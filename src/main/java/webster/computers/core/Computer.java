package webster.computers.core;

import webster.util.Maps;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Computer {

    public static final int MAX_COMPUTERS_PER_PAGE = 10;

    private static Map<Long, Computer> computers = new ConcurrentHashMap<>();

    static {
        List<Computer> randomComputers = randomComputers(42);
        randomComputers.stream().forEach(Computer::store);
    }

    public static void store(Computer computer) {
        computers.put(computer.id(), computer);
    }

    public static void delete(long id) {
        computers.remove(id);
    }

    public static Optional<Computer> byId(long id) {
        return Optional.ofNullable(computers.get(id));
    }

    public static Page<Computer> loadPage(int index, Optional<String> sortBy, boolean desc, Optional<String> filter) {
        List<Computer> computers = matchingComputers(sortBy, desc, filter).collect(Collectors.toList());
        int minIndex = index * MAX_COMPUTERS_PER_PAGE;
        int maxIndex = Math.min(computers.size(), minIndex + MAX_COMPUTERS_PER_PAGE);
        return new Page<>(computers.subList(minIndex, maxIndex), MAX_COMPUTERS_PER_PAGE, computers.size(), index);
    }

    private static Stream<Computer> matchingComputers(Optional<String> sortBy, boolean desc, Optional<String> filter) {
        return computers.values().stream()
                .filter(buildPredicate(filter))
                .sorted(buildComparator(sortBy, desc));
    }

    private static Comparator<Computer> buildComparator(Optional<String> sortBy, boolean desc) {
        Map<String, Function<Computer, Comparable>> extractors = Maps.<String, Function<Computer, Comparable>>newMap()
                .with("name", Computer::name)
                .with("introduced", Computer::introduced)
                .with("discontinued", c -> c.discontinued.isPresent() ? c.discontinued().get() : new Date(Long.MAX_VALUE))
                .with("company", Computer::company)
                .build();

        if (sortBy.isPresent() && extractors.keySet().contains(sortBy.get())) {
            Function<Computer, Comparable> keyExtractor = extractors.get(sortBy.get());
            Comparator<Computer> comparator = Comparator.comparing(keyExtractor);
            return desc ? comparator.reversed() : comparator;
        } else {
            return (o1, o2) -> 0;
        }
    }

    private static Predicate<Computer> buildPredicate(Optional<String> filter) {
        return filter.isPresent()
                ? c -> c.name.startsWith(filter.get())
                : c -> true;
    }

    private static List<Computer> randomComputers(int count) {
        return Stream.generate(Computer::randomComputer).limit(count).collect(Collectors.toList());
    }

    private static Computer randomComputer() {
        Random random = new Random();
        long tenYearsMillis = 1000l * 60l * 60l * 24l * 365l * 10l;
        long neg = -randLong(random, tenYearsMillis);
        Date introduced = new Date(System.currentTimeMillis() + neg);
        Date discontinued = random.nextBoolean() ? null : new Date(System.currentTimeMillis() + (neg / randomFrom(random, 2, 3, 4)));
        String name = randomFrom(random, "tick", "trick", "track", "chewbacca", "luke", "darth", "mario", "luigi");
        return new Computer(-neg, name + neg, introduced, Optional.ofNullable(discontinued), name + "'s company");
    }

    // see http://stackoverflow.com/a/2546186
    private static long randLong(Random random, long bound) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % bound;
        } while (bits - val + (bound - 1) < 0L);
        return val;
    }

    @SafeVarargs
    private static <T> T randomFrom(Random random, T... items) {
        return items[random.nextInt(items.length)];
    }
    private final long id;
    private final String name;
    private final Date introduced;
    private final Optional<Date> discontinued;
    private final Date lastChangeDate;

    private final String company;

    public Computer(long id, String name, Date introduced, Optional<Date> discontinued, String company) {
        this.id = id;
        this.name = name;
        this.introduced = introduced;
        this.discontinued = discontinued;
        this.company = company;
        this.lastChangeDate = new Date();
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Date introduced() {
        return introduced;
    }

    public Optional<Date> discontinued() {
        return discontinued;
    }

    public String company() {
        return company;
    }

    public Date lastChangeDate() {
        return lastChangeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Computer computer = (Computer) o;

        if (id != computer.id) return false;
        if (!company.equals(computer.company)) return false;
        if (!discontinued.equals(computer.discontinued)) return false;
        if (!introduced.equals(computer.introduced)) return false;
        if (!name.equals(computer.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + introduced.hashCode();
        result = 31 * result + discontinued.hashCode();
        result = 31 * result + company.hashCode();
        return result;
    }
}
