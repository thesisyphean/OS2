import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
import numpy as np
import sys
import subprocess

ALGORITHMS = ["FCFS", "SJF", "RR"]
NAMES = ["First-Come-First-Served", "Shortest-Job-First", "Round Robin"]
PATRONS = [50, 100, 200]
ITERATIONS = 3
VARIABLES = ["TurnaroundAvg", "WaitingTime", "ResponseTime"]
VAR_NAMES = ["Tunaround Time", "Waiting Time", "Response Time"]


def main():
    if len(sys.argv) > 1 and sys.argv[1] == "execute":
        execute()
        return

    # sns.set_theme(style="whitegrid")

    averages = {
        "TurnaroundAvg": {50: [], 100: [], 200: []},
        "WaitingTime": {50: [], 100: [], 200: []},
        "ResponseTime": {50: [], 100: [], 200: []},
    }

    stds = {
        "TurnaroundAvg": {50: [], 100: [], 200: []},
        "WaitingTime": {50: [], 100: [], 200: []},
        "ResponseTime": {50: [], 100: [], 200: []},
    }

    # Calculate all of the averages and standard deviations
    for i in range(len(ALGORITHMS)):
        for patron_num in PATRONS:
            # Load all the dataframes
            dfs = [
                pd.read_csv(f"dat/{ALGORITHMS[i]}_{patron_num}_{j}.csv")
                for j in range(ITERATIONS)
            ]
            # Join all iterations
            df = pd.concat(dfs, ignore_index=True)

            for variable in VARIABLES:
                averages[variable][patron_num].append(df[variable].mean())
                stds[variable][patron_num].append(df[variable].std())

    plot_values_per_variable("Average", averages)
    plot_values_per_variable("Standard Deviation of", stds)

    for i in range(len(ALGORITHMS)):
        algorithm = ALGORITHMS[i]
        # Load all of the data files (only with 100 patrons though)
        dfs = [pd.read_csv(f"dat/{algorithm}_100_{j}.csv") for j in range(ITERATIONS)]
        # Concatenate them to dilute outliers
        df = pd.concat(dfs, ignore_index=True)

        for j in range(len(VARIABLES)):
            plt.cla()
            variable = VARIABLES[j]
            var_name = VAR_NAMES[j]

            # Split into 8 possible "bins" to count frequency
            cutoff = df[variable].max() / 8.0
            # Sort the values into bins
            df["Split"] = pd.cut(
                df[variable],
                bins=range(0, df[variable].max(), int(cutoff)),
                right=False,
            )
            split_counts = df["Split"].value_counts().sort_index()
            # Convert [left, right) into just the righ value to look nice
            xlabels = [
                split_count.right for split_count in split_counts.index.to_list()
            ]
            ax = sns.barplot(x=xlabels, y=split_counts.values)

            plt.title(
                f"Distribution of {var_name} for {NAMES[i]} with 100 patrons\n"
            )
            plt.xlabel(f"{var_name}")
            plt.ylabel("Frequency")
            plt.tight_layout()

            filename = f"{variable} for {algorithm}"
            plt.savefig(f"plt/{filename}.png", bbox_inches="tight", dpi=100)

        # Determining throughput
        # We find the max end time, divide it by 10 to create 10 logical sections
        # Then find the number of patrons that left in each logical section
        # sdf = dfs[0]
        # sdf["EndTime"] = sdf["ArrivalTime"] + sdf["TotalTime"]
        # Size of each logical section
        # cutoff = sdf["EndTime"].max() / 10.0

        # Logical sections
        # sdf["Split"] = pd.cut(sdf["EndTime"], bins=range(0, sdf["EndTime"].max(), int(cutoff)), right=False)
        # Number of processes ended in each section
        # split_counts = sdf["Split"].value_counts().sort_index()
        # print(split_counts)
        # sns.barplot(x=split_counts.index, y=split_counts.values)
        # plt.xlabel('Chunk')
        # plt.ylabel('Number of End Times')
        # plt.ylim(0, 10)
        # plt.title('Number of End Times in Each Chunk')
        # plt.show()

        print(f"Plotted {algorithm}")


def plot_values_per_variable(value_type, values):
    for i, (var, data) in enumerate(values.items()):
        plt.figure()
        plt.title(f"{value_type} {VAR_NAMES[i]}")
        bar_width = 0.2
        index = np.arange(len(PATRONS))
    
        # Plot each algorithm instead of each set of bars
        for j in range(len(ALGORITHMS)):
            # i.e. these values must be all from the same algorithm
            values = [data[patron_num][j] / 1000.0 for patron_num in PATRONS]
            plt.bar(index + j * bar_width, values, bar_width, label=f"{NAMES[j]}")

        plt.xlabel('Patrons')
        plt.ylabel('Seconds')
        plt.xticks(index + 0.5 * (len(data) - 1) * bar_width, PATRONS)
        plt.legend()
        plt.savefig(f"plt/{value_type} {VAR_NAMES[i]}.png")


def execute():
    for a in range(len(ALGORITHMS)):
        for num_patrons in PATRONS:
            for i in range(ITERATIONS):
                subprocess.run(
                    f"make run {num_patrons} {a} {ALGORITHMS[a]}_{num_patrons}_{i}",
                    shell=True,
                )


if __name__ == "__main__":
    main()
