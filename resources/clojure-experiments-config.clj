{:cli   [["-h" "--help"]
	 ["-a" "--all" :default false]
         ["-n" "--none" :default false]]
 :usage [:green :bold "A brief description of the program goes here."
         \newline
	 \newline
         :green :bold "Usage: "
	 :green "clojure-experiments [options] actions"
         \newline
	 \newline
         :green :bold "Options:"
         \newline
	 :green "##summary##"
	 \newline
	 \newline
         :green :bold "Actions:"]
 :error [:red "The following errors occurred while parsing your command:"
         \newline
         \newline]}
