# ╥─────────────────────────────╥
# ║ IUS COPIANDI MMXVII Vexatos ║
# ║ NON CURO QUOD HAC RE FACIS. ║
# ╨─────────────────────────────╨

#=
Usage:
    `julia howmanyblocks.jl 500000`
tells you how many blocks of which compression level you get for 500000 RF.
Any parseable expression works here.
    `julia howmanyblocks.jl 500_000`
    `julia howmanyblocks.jl "2^63-1`
    `julia howmanyblocks.jl "factorial(big(1000))"`
=#

# Change this to whatever your config says for the RF/FE energy multiplier
factor = 20

function work()
    energy = big(eval(parse(join(ARGS, " "))))
    blocks = nothing
    level = 1
    cost(level) = level == 0 ? 0 : cost(level - 1) * 9 + factor * factorial(big(level))
    while true
        if cost(level) > energy
            level -= 1
            blocks = zeros(Int64, level)
            break
        end
        level += 1
    end
    while level > 0
        req = cost(level)
        blocks[level] = floor(energy / req)
        energy -= floor(energy / req) * req
        level -= 1
    end
    for i = length(blocks):-1:1
        println("Level $i: $(blocks[i]) time$(blocks[i] == 1 ? "" : "s")")
    end
end
@time work()
