import os;
mod_id = "axiology"
resource_dir = "/home/autocadeng/IdeaProjects/Alchemy/src/main/resources/assets/"

core_axioms = {
    "cohesio",
    "composito",
    "proprietera",
    "radiato",
    "gravitas",
    "polaris",
    "vitea",
    "calor",
    "mutatio",
    "ordo",
    "soluto",
    "motus",
    "crescentia",
    "forma",
    "inversio",
    "potentia",
    "trans"
}

def main():
    for axiomName in core_axioms:
        removeAxiom(axiomName)

def removeAxiom(axiomName):
    file_paths = [
        f"{resource_dir}{mod_id}/blockstates/{axiomName}.json",
        f"{resource_dir}{mod_id}/models/block/{axiomName}.json",
        f"{resource_dir}{mod_id}/models/block/{axiomName}_flowing.json",
        f"{resource_dir}{mod_id}/models/item/{axiomName}_shard.json",
        f"{resource_dir}{mod_id}/models/item/{axiomName}_bucket.json"
    ]

    for path in file_paths:
        try:
            os.remove(path)
            print(f"Successfully deleted: {path}")
        except FileNotFoundError:
            print(f"Skipped (File not found): {path}")
        except PermissionError:
            print(f"Error (Permission denied): {path}")

if __name__ == "__main__":
    main()