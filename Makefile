MAIN_CLASS = SchedulingSimulation
MAIN_CLASSFILE = bin/$(MAIN_CLASS).class

DEPS = DrinkOrder Barman Patron
DEPS_CLASSFILES = $(DEPS:%=$(BINDIR)/%.class)

$(MAIN_CLASSFILE): $(DEPS_CLASSFILES)

bin/%.class: src/%.java
	javac -d bin -sourcepath src -cp bin $<

run: $(MAIN_CLASSFILE)
	java -cp bin $(MAIN_CLASS)

clean:
	rm bin/*.class
