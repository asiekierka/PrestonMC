-- Basic Chinese characters for digits
local digits = {"一", "二", "三", "四", "五", "六", "七", "八", "九"}
-- Set zero to empty string to avoid concat nil
digits[0] = ""
-- Deliminator for arithmetic carry
local ten = "十"
local hundred = "百"
local thousand = "千"
local tenThousand = "万"
local hundredMillion = "亿"

local upperLimit = arg[1] or 1000
local dump = io.open("result.txt", "w+")

for num = 1, upperLimit do
  local line = ""
  if (num < 10) then
    line = digits[num]
  elseif (num < 20) then
    line = ten .. digits[num % 10]
  elseif (num < 100) then
    line = digits[num // 10] .. ten .. digits[num % 10]
  elseif (num < 1000) then
    line = digits[num // 100] .. hundred
    if (num % 100 // 10 ~= 0) then
      line = line .. digits[num % 100 // 10] .. ten .. digits[num % 10]
    elseif (num % 100 ~= 0) then
      line = line .. "零" .. digits[num % 10]
    end
  end
  -- TODO support arbitrary number
  -- Arabic number is also written for convenience
  dump:write(tostring(num) .. " " .. line .. "\n")
end

dump:flush()
dump:close()
