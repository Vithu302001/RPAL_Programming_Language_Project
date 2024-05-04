import java.util.HashMap;
import java.util.Map;

class Environment {
    private int name;
    private Map<String, Integer> binding;
    private Environment parent;
    private Environment child;

    public Environment(int name) {
        this.name = name;
        this.binding = new HashMap<>();
        this.parent = null;
        this.child = null;
    }

    public void addChild(Environment env) {
        this.child = env;
        env.parent = this;
    }

    public Integer lookup(String var) {
        if (binding.containsKey(var)) {
            return binding.get(var);
        } else {
            if (parent == null) {
                System.out.println(var + " not defined.");
                return null;
            } else {
                return parent.lookup(var);
            }
        }
    }

    public void addBinding(String var, int value) {
        binding.put(var, value);
    }
}
