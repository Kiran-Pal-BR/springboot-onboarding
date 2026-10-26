1. General Characteristics

Features:
a) Type System
Java: Statically Typed (Resolved at Compile Time) 
Python: Dynamically Typed (Resolved at Runtime)

b) Execution:
Java : Compiled to Bytecode (JVM Execution)
Python : Interpreted / Bytecode

c) Null Representation:
Java: null (Safely handled via Optional<T>)
Python: None

d) Data Immutability:
Java:record (Java 14+)
Python: @dataclass(frozen=True) / Named Tuple

=========================================================================

2. Basic Syntax & Variables
Python:
   name: str = "Alice"
   age: int = 25
   prices: list [float] = [10.5, 20.0]

Java:
    String name = "Alice";
    int age = 25;
    List<Double> prices = List.of(10.5, 20.0);

============================================================================

3. Collections & Data Structures
Data Structure: 
a) Dynamic Array
Python:list ([1, 2, 3])
Java: List<T> (ArrayList<T>)

b) Key-Value Pair
Python: dict ({"key": "val"})
Java: Map<K, V> (HashMap<K, V>)

c) Unique Set
Python:set ({1, 2, 3})
Java: Set<T> (HashSet<T>)

d) O(1) Lookup
Python:val = my_dict.get("key")
Java: V val = myMap.get("key");

=============================================================================
4. Functional Operations & Stream Processing

Python (List Comprehension & Lambda)
   nums = [1, 2, 3, 4, 5]
   evens = [n for n in nums if n % 2 == 0]
   double_evens = list(map(lambda x: x * 2, evens))

Java (Streams API)
    List<Integer> nums = List.of(1, 2, 3, 4, 5);
    List<Integer> doubleEvens = nums.stream()
    .filter(n -> n % 2 == 0)
    .map(x -> x * 2)
    .toList();

============================================================================
5. Exception Handling

Python:
    try:
    result = amount / divider
    except ZeroDivisionError as e:
    print(f "Error: {e}")

Java:
    try {
    BigDecimal result = amount.divide(divider);
    } catch (ArithmeticException e) {
    log.error("Calculation failed: {}", e.getMessage());
    }

============================================================================
6. Financial & Precision Calculations

Python:
    from decimal import Decimal
    amount = Decimal("100.50") + Decimal("20.25")
Java:
    BigDecimal amount = new BigDecimal("100.50").add(new BigDecimal("20.25"));


