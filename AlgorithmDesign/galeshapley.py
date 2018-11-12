# The Gale-Shapley algorithm

import numpy as np

# Number of people in both sets.
PEOPLE = 100

# Create sets of men and women.
men = np.arange(0, PEOPLE).tolist()
women = np.arange(0, PEOPLE).tolist()

print('Men count: {0}'.format(PEOPLE))
print('Women count: {0}'.format(PEOPLE))

# Men's preferation lists.
men_prefer_list = dict()
women_prefer_list = dict()

# Generate preferation lists.
for i in men:
    woman_list = np.arange(0, len(women))
    np.random.shuffle(woman_list)
    men_prefer_list[i] = woman_list.tolist()
    print('Man {0} prefer order: {1}'.format(i, woman_list))

for i in women:
    man_list = np.arange(0, len(men))
    np.random.shuffle(man_list)
    women_prefer_list[i] = man_list.tolist()
    print('Woman {0} prefer order: {1}'.format(i, man_list))


def prefers(prefer_list, person1, person2):
    """Return true if man/woman prefers person1 to person2"""
    person1_index = prefer_list.index(person1)
    person2_index = prefer_list.index(person2)
    return person1_index < person2_index


def not_proposed_to_every_woman(men, women, men_preferations_left):
    """Return list of men who have not proposed to every woman."""
    return [man for man in men if len(men_preferations_left[man]) > 0]


def propose(man, woman, woman_partner, woman_prefer_list):
    """Man proposes to woman, return True if proposal successful."""
    proposer_index = woman_prefer_list.index(man)
    current_index = woman_prefer_list.index(woman_partner)
    return proposer_index > current_index


def gale_shapley(men: list, women: list, men_preferations: dict, women_preferations: dict) -> (dict, dict):
    """
    Gale-Shapley algorithm which produces a stable match between set of men and women.

    :param men: List of men's identifiers.
    :param women: List of women's identifiers.
    :param men_preferations: Dictionary of lists of men's preferation lists to women.
    :param women_preferations: Dictionary of lists of women's preferation lists to men.
    :return: List of tuples which represent the matches from women to men.
    """

    # List of free men.
    free_men = men.copy()

    # Proposals.
    men_preferations = men_preferations.copy()

    # Women's current partners.
    women_parners = dict()

    print('Starting Gale-Shapley')

    while len(free_men) > 0 and len(not_proposed_to_every_woman(free_men, women, men_preferations)) > 0:
        free_man = free_men[0]
        next_woman = men_preferations[free_man].pop(0)

        print('Free man {0}, attempting woman {1}'.format(free_man, next_woman))

        proposal_success = False
        # Free man proposes to next preferred woman.
        if next_woman not in women_parners:
            proposal_success = True
            print('Woman {0} is free'.format(next_woman))
        else:
            woman_previous_partner = women_parners[next_woman]
            print('Man {0} proposes to non-free woman {1} with previous partner {2}'.format(
                free_man
                , next_woman
                , woman_previous_partner))

            proposal_success = propose(free_man
                                       , next_woman
                                       , woman_previous_partner
                                       , women_preferations[next_woman])
            if proposal_success:
                print('Proposal was successful')
                # The previous partner of the woman becomes free.
                free_men.append(woman_previous_partner)
                print('{0} becomes free'.format(woman_previous_partner))
            else:
                print('Proposal was unsuccessful')

        if proposal_success:
            # Man becomes non-free.
            free_men.pop(0)
            women_parners[next_woman] = free_man
            print('Man {0} becomes engaged to woman {1}'.format(free_man, next_woman))

    # Return list of tuples which is the list of stable matches.
    return women_parners.items()


# Run algorithm.
results = gale_shapley(men, women, men_prefer_list, women_prefer_list)
print('Result: {0}'.format(results))
