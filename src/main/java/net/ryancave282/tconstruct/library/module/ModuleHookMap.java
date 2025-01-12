package net.ryancave282.tconstruct.library.module;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import lombok.RequiredArgsConstructor;
import slimeknights.mantle.data.loadable.ErrorFactory;
import net.ryancave282.tconstruct.TConstruct;
import net.ryancave282.tconstruct.library.modifiers.impl.BasicModifier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Logic for handling modifier and tool hooks, automatically fetching the default instance as needed. */
@SuppressWarnings({"ClassCanBeRecord", "unused"}) // no record as we don't want the map to be public
@RequiredArgsConstructor
public class ModuleHookMap {
  /** Instance with no modifiers */
  public static final ModuleHookMap EMPTY = new ModuleHookMap(Collections.emptyMap());

  /** Internal map of modifier hook to object. It's the caller's responsibility to make sure the object is valid for the hook */
  private final Map<ModuleHook<?>,Object> modules;

  /**
   * Creates a modifier hook map from the given module list
   * @param modules  List of modules
   * @return  Modifier hook map
   */
  public static ModuleHookMap createMap(List<? extends WithHooks<?>> modules, ErrorFactory error) {
    if (modules.isEmpty()) {
      return EMPTY;
    }
    Builder builder = builder();
    for (WithHooks<?> withHooks : modules) {
      HookProvider module = withHooks.module();
      for (ModuleHook<?> hook : withHooks.getModuleHooks()) {
        builder.addHookChecked(module, hook, error);
      }
      module.addModules(builder);
    }
    return builder.build();
  }

  /** Checks if a module is registered for the given hook */
  public boolean hasHook(ModuleHook<?> hook) {
    return modules.containsKey(hook);
  }

  /** Gets the module matching the given hook, or null if not defined */
  @SuppressWarnings("unchecked")
  @Nullable
  public <T> T getOrNull(ModuleHook<T> hook) {
    return (T)modules.get(hook);
  }

  /** Gets the module matching the given hook */
  public <T> T getOrDefault(ModuleHook<T> hook) {
    T object = getOrNull(hook);
    if (object != null) {
      return object;
    }
    return hook.getDefaultInstance();
  }

  /** Gets an unchecked view of all internal modules for the sake of serialization */
  public Map<ModuleHook<?>,Object> getAllModules() {
    return modules;
  }

  /** Creates a new builder instance */
  public static ModuleHookMap.Builder builder() {
    return new ModuleHookMap.Builder();
  }

  @SuppressWarnings("UnusedReturnValue")
  public static class Builder {
    private final ErrorFactory ILLEGAL_ARGUMENT = IllegalArgumentException::new;
    /** Builder for the final map */
    private final LinkedHashMultimap<ModuleHook<?>,Object> modules = LinkedHashMultimap.create();

    private Builder() {}

    /**
     * Adds a module to the builder, validating it at runtime. Used for JSON parsing
     * @throws IllegalArgumentException  if the hook type is invalid
     */
    public Builder addHookChecked(Object object, ModuleHook<?> hook) {
      return addHookChecked(object, hook, ILLEGAL_ARGUMENT);
    }

    /**
     * Adds a module to the builder, validating it at runtime. Used for JSON parsing
     * @throws RuntimeException  if the hook is type in invalid matching the given factory
     */
    public Builder addHookChecked(Object object, ModuleHook<?> hook, ErrorFactory error) {
      if (hook.isValid(object)) {
        modules.put(hook, object);
      } else {
        throw error.create("Object " + object + " is invalid for hook " + hook);
      }
      return this;
    }

    /** Adds a modifier module to the builder, automatically adding all its hooks. Use {@link #addHook(Object, ModuleHook)} to specify hooks. */
    public Builder addModule(HookProvider module) {
      for (ModuleHook<?> hook : module.getDefaultHooks()) {
        addHookChecked(module, hook);
      }
      module.addModules(this);
      return this;
    }

    /** Adds a module to the builder */
    public <H, T extends H> Builder addHook(T object, ModuleHook<H> hook) {
      modules.put(hook, object);
      return this;
    }

    /** Adds a module to the builder that implements multiple hooks */
    public <T> Builder addHook(T object, ModuleHook<? super T> hook1, ModuleHook<? super T> hook2) {
      addHook(object, hook1);
      addHook(object, hook2);
      return this;
    }

    /** Adds a module to the builder that implements multiple hooks */
    public <T> Builder addHook(T object, ModuleHook<? super T> hook1, ModuleHook<? super T> hook2, ModuleHook<? super T> hook3) {
      addHook(object, hook1);
      addHook(object, hook2);
      addHook(object, hook3);
      return this;
    }

    /** Adds a module to the builder that implements multiple hooks */
    @SafeVarargs
    public final <T> Builder addHook(T object, ModuleHook<? super T>... hooks) {
      // TODO 1.20: change signature to addHook(Object, ModuleHook, ModuleHook...) and ditch this error
      if (hooks.length == 0) {
        TConstruct.LOG.error("Module {} added with no hooks, this is a bug in the mod adding it as it does nothing, and will not be allowed in the future.", object, new IllegalArgumentException("Empty hooks list passed to hook map builder"));
      }
      for (ModuleHook<? super T> hook : hooks) {
        addHook(object, hook);
      }
      return this;
    }

    /** Helper to deal with generics */
    @SuppressWarnings("unchecked")
    private static <T> void insert(ImmutableMap.Builder<ModuleHook<?>,Object> builder, ModuleHook<T> hook, Collection<Object> objects) {
      if (objects.size() == 1) {
        builder.put(hook, objects.iterator().next());
      } else if (!objects.isEmpty()) {
        builder.put(hook, hook.merge((Collection<T>)objects));
      }
    }

    /** Builds the final map */
    public ModuleHookMap build() {
      if (modules.isEmpty()) {
        return EMPTY;
      }
      ImmutableMap.Builder<ModuleHook<?>,Object> builder = ImmutableMap.builder();
      for (Entry<ModuleHook<?>,Collection<Object>> entry : modules.asMap().entrySet()) {
        insert(builder, entry.getKey(), entry.getValue());
      }
      return new ModuleHookMap(builder.build());
    }

    /** Transitions this builder into a basic modifier builder */
    public BasicModifier.Builder modifier() {
      return BasicModifier.Builder.builder(build());
    }
  }

}
