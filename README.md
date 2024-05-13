# OS2

## Description

An experiment simulating and analysing different process scheduling algorithms such as FCFS and SJF. Round Robin is another scheduling algorithm that is implemented. Different statistical variables are compared for the different algorithms and graphed in the included report.

The CPU is seen as a barman, taking drink orders (tasks) from patrons (processes) that enter and exit the bar. The barman can determine the order in which to complete the orders using different algorithms. In the code, the language is used interchangeably. For example, patrons are referred to as processes and vice versa.

## Compilation and Execution

`make` can be used to compile the code, followed by `make run` to execute it. Commandline arguments can be passed through `make` like so: `make run <number_of_patrons> <scheduling_algorithm> <output_filename>`, where `<scheduling_algorithm>` should be 0 for FCFS, 1 for SJF and 2 for RR. The output files will be saved to the `dat` directory.

`plot.py` has been created to plot the data. Run with no commandline arguments, it will plot all of the necessary graphs. Run as `plot.py execute`, it will run the experiment for various numbers of patrons, algorithms and iterations.
