SRCDIR = src
BINDIR = bin

MAIN_CLASS = SchedulingSimulation
MAIN_CLASSFILE = $(BINDIR)/$(MAIN_CLASS).class

DEPS = DrinkOrder Barman Patron
DEPS_CLASSFILES = $(DEPS:%=$(BINDIR)/%.class)

$(MAIN_CLASSFILE): $(DEPS_CLASSFILES)

$(BINDIR)/%.class: $(SRCDIR)/%.java
	javac -d "$(BINDIR)" -sourcepath "$(SRCDIR)" -cp "$(BINDIR)" $<

run: $(MAIN_CLASSFILE)
	java -cp "$(BINDIR)" $(MAIN_CLASS)

clean:
	rm $(BINDIR)/*.class
