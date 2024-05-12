# If the first argument is "run"...
ifeq (run,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif

MAIN_CLASS = SchedulingSimulation
MAIN_CLASSFILE = bin/$(MAIN_CLASS).class

DEPS = DrinkOrder Barman Patron
DEPS_CLASSFILES = $(DEPS:%=bin/%.class)

$(MAIN_CLASSFILE): $(DEPS_CLASSFILES)

bin/%.class: src/%.java
	javac -d bin -sourcepath src -cp bin $<

.PHONY: run
run: $(MAIN_CLASSFILE)
	java -cp bin $(MAIN_CLASS) $(RUN_ARGS)

.PHONY: clean
clean:
	rm bin/*.class
