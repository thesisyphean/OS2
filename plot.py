import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
import sys
import subprocess

ALGORITHMS = ["FCFS", "SJF", "RR"]
NAMES = ["First-Come-First-Served", "Shortest-Job-First", "Round Robin"]
PATRONS = [50, 100, 200]
ITERATIONS = 3

def main():
    if len(sys.argv) > 1 and sys.argv[1] == "execute":
        execute()
        return

    sns.set_theme(style="whitegrid")

    for i in range(len(ALGORITHMS)):
        for patron_num in PATRONS:
            algorithm = ALGORITHMS[i]
            dfs = [pd.read_csv(f"dat/{algorithm}_{patron_num}_{j}.csv") for j in range(ITERATIONS)]
            df = pd.concat(dfs, ignore_index=True)
    
            for variable in ["TurnaroundAvg", "WaitingTime", "ResponseTime"]:
                print(f"----- {variable} -----")
                print(f"Average: {df[variable].mean()}")
                print(f"Standard Deviation: {df[variable].std()}")
                sns.lineplot(data=df, x="ArrivalTime", y=f"{variable}")
                # sns.histplot(data=df, x="ArrivalTime", y=f"{variable}", kde=True)

                plt.title(f"Distribution of {variable} for {NAMES[i]}\n")
                plt.xlabel(f"{variable}")
                plt.ylabel("Frequency")

                filename = f"{variable} for {algorithm}"
                plt.savefig(f"plt/{filename}.png", bbox_inches="tight", dpi=100)
            
            # Determining throughput
            sdf = dfs[0]
            sdf["EndTime"] = sdf["ArrivalTime"] + sdf["TotalTime"]
            cutoff = sdf["EndTime"].max() / 10.0

            sdf["Split"] = pd.cut(sdf["EndTime"], bins=range(0, sdf["EndTime"].max(), int(cutoff)), right=False)
            split_counts = sdf["Split"].value_counts().sort_index()
            print(split_counts)
            sns.barplot(x=split_counts.index, y=split_counts.values)
            plt.xlabel('Chunk')
            plt.ylabel('Number of End Times')
            plt.ylim(0, 10)
            plt.title('Number of End Times in Each Chunk')
            plt.show()

            print(f"Plotted {algorithm}")

def execute():
    for a in range(len(ALGORITHMS)):
        for num_patrons in PATRONS:
            for i in range(ITERATIONS):
                subprocess.run(f"make run {num_patrons} {a} {ALGORITHMS[a]}_{num_patrons}_{i}", shell=True)

if __name__ == "__main__":
    main()