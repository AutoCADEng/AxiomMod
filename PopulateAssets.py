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
        addAxiom(axiomName)

def addAxiom(axiomName):
    with open(f"{resource_dir}{mod_id}/blockstates/{axiomName}.json","w") as f:

        f.write(f"""{{
  "variants": {{
    "": {{
      "model": "{mod_id}:block/{axiomName}"
    }}
  }}
}}""")

    with open(f"{resource_dir}{mod_id}/models/block/{axiomName}.json", "w") as f:
        f.write(f"""{{
  "textures": {{
    "particle": "{mod_id}:block/fluid/generic_still"
  }}
}}""")

    with open(f"{resource_dir}{mod_id}/models/block/{axiomName}_flowing.json", "w") as f:
        f.write(f"""{{
  "textures": {{
    "particle": "{mod_id}:block/fluid/generic_flowing"
  }}
}}""")
    with open(f"{resource_dir}{mod_id}/models/item/{axiomName}_shard.json", "w") as f:
            f.write(f"""{{
  "parent": "{mod_id}:item/axiom_shard"
}}""")

    with open(f"{resource_dir}{mod_id}/models/item/{axiomName}_bucket.json", "w") as f:
            f.write(f"""{{
  "parent": "minecraft:item/generated",
  "textures": {{
    "layer0": "{mod_id}:item/generic_bucket",
    "layer1": "{mod_id}:item/generic_bucket_mask"
  }}
}}""")

if __name__ == "__main__":
    main()