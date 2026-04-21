"The Real Heat." Documented Shellbags, USBSTOR, and Registry artifacts.
[CASE ID]: [Project Name]
# Summary: Investigated a compromised Windows 10 environment to identify the artifacts left by a threat actor.

Identified a persistent reverse shell in a compromised Windows 10 image. 

Scope: What artifacts did you analyze?

Methodology: Which tools?

Key Evidence (The Stains):

Artifact 1:  Was found suspicious IP 10.x.x.x in the Prefetch files.

Artifact 2: Identified persistence via the 'soft_run' Registry key.

Remediation:

Method: Performed memory and registry analysis to reconstruct an attack timeline.

Tools: Volatility 3, FTK Imager, Registry Explorer.

Artifacts: Identified a reverse shell calling back to a C2 server via a hidden registry key.

Screenshot: [Image of Registry Analysis or Timeline here]
