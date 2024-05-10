import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
import sys

def main():
    df = pd.read_csv("turnaround_time_0.txt")

    # sns.set_theme()
    sns.set_theme(style="whitegrid")

    sns.histplot(data=df, x='TotalTime', kde=True)
    plt.title('Distribution of Values')
    plt.xlabel('Value')
    plt.ylabel('Frequency')
    plt.show()
    # plt.savefig(f"dat/{host}_{i + 1}.png", bbox_inches="tight", dpi=100)

if __name__ == "__main__":
    main()