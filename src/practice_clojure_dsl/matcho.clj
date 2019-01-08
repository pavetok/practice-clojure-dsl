(ns practice-clojure-dsl.matcho
  (:require
    [clojure.spec.alpha :as s]))

(defn smart-explain-data [p x]
  (cond
    (instance? clojure.spec.alpha.Specize p)
    (when-not (s/valid? p x)
      {:expected (vec (s/describe p)) :but (s/explain-data p x)})

    (and (string? x) (instance? java.util.regex.Pattern p))
    (when-not (re-find p x)
      {:expected (str "match regexp: " p) :but x})

    (fn? p)
    (when-not (p x)
      {:expected (pr-str p) :but x})

    (and (keyword? p) (s/get-spec p))
    (let [sp (s/get-spec p)]
      (when-not (s/valid? p x)
        {:expected (str "conforms to spec: " p) :but (s/explain-data p x)}))

    :else (when-not (= p x)
            {:expected p :but x})))

(defn- match-recur [errors path x pattern]
  (cond
    (and (map? x)
         (map? pattern))
    (let [strict? (:matcho/strict (meta pattern))
          errors (if (and strict? (not (= (set (keys pattern))
                                          (set (keys x)))))
                   (conj errors {:expected "Same keys in pattern and x"
                                 :but (str "Got " (vec (keys pattern))
                                           " in pattern and " (vec (keys x)) " in x")
                                 :path path})
                   errors)]
      (reduce (fn [errors [k v]]
                (let [path (conj path k)
                      ev (get x k)]
                  (match-recur errors path ev v)))
              errors pattern))

    (and (coll? x)
         (not (map? x))
         (vector? pattern))
    (let [strict? (:matcho/strict (meta pattern))
          errors (if (and strict? (not (= (count pattern) (count x))))
                   (conj errors {:expected "Same number of elements in sequences"
                                 :but (str "Got " (count pattern)
                                           " in pattern and " (count x) " in x")
                                 :path path})
                   errors)]
      (reduce (fn [errors [k v]]
                (let [path (conj path k)
                      ev (nth (vec x) k nil)]
                  (match-recur errors path ev v)))
              errors
              (map (fn [x i] [i x]) pattern (range))))

    (and (vector? pattern)
         (:matcho/or (meta pattern)))
    (let [mismatches (take-while not-empty (map #(match-recur [] path x %) pattern))]
      (if (= (count pattern) (count mismatches))
        (vec (apply concat (cons errors mismatches)))
        errors))

    :else (let [err (smart-explain-data pattern x)]
            (if err
              (conj errors (assoc err :path path))
              errors))))

(comment
  (apply concat (map #(match-recur [] [] 0 %) [0 1]))
  (match-recur [] [] 0 [0])
  (match-recur [] [] 0 ^:matcho/or [0 1])
  (match-recur [] [] 0 ^:matcho/or [1 0])
  (match-recur [] [] 0 ^:matcho/or [1 2])
  (match-recur [] [] [0] ^:matcho/or [0 1])
  (do))

(defn match*
  "Match against each pattern"
  [x & patterns]
  (reduce (fn [acc pattern] (match-recur acc [] x pattern)) [] patterns))

(comment
  (match* 0 ^:matcho/or [0 1])
  (match* 0 ^:matcho/or [1 2])
  (match* 0 ^:matcho/or [{:a 0} {:b 1}])
  (match* {:a 0} {:a 0})
  (match* {:a 0} {})
  (match* 0 {})
  (match* {:a {:b 0}} {:a {:b 1}})
  (match* {:a 0} [{:a 0}])
  (match* {:a 0} ^:matcho/or [{:a 0} {:b 1}])
  (match* {:a 0} ^:matcho/or [{:a 1} {:a 2}])
  (match* ["a"] ^:matcho/or ["b"])
  (match* "a" ^:matcho/or ["a"])
  (match* "a" ["a"])
  (do))